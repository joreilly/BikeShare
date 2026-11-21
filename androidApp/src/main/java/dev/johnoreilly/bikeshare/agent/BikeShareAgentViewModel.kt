package dev.johnoreilly.bikeshare.agent

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.genai.agent.Agent
import com.google.mlkit.genai.agent.AgentEvent
import com.google.mlkit.genai.agent.AgentRunner
import com.google.mlkit.genai.agent.AgentSession
import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.Content
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.GenerativeModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * "Ask BikeShare": an on-device agent (ML Kit GenAI agent API, Gemini Nano via AICore) whose tools
 * are the app's own AppFunctions in [dev.johnoreilly.bikeshare.functions.BaseBikeShareAppFunctionService],
 * the same functions the system exposes to assistants.
 *
 * Needs Android 16 (AppFunctions), a device that supports Gemini Nano custom generation, and a
 * Google account enrolled in the AICore Developer Preview. Anywhere else [availability] ends up
 * [Availability.Unsupported] and the UI hides the entry point.
 */
class BikeShareAgentViewModel(application: Application) : AndroidViewModel(application) {

    sealed interface Availability {
        data object Checking : Availability
        /** Gemini Nano is downloading; [bytes] so far, when known. */
        data class Downloading(val bytes: Long?) : Availability
        data object Ready : Availability
        data class Unsupported(val reason: String) : Availability
    }

    private val _availability = MutableStateFlow<Availability>(Availability.Checking)
    val availability: StateFlow<Availability> = _availability.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val agent = Agent.builder()
        .setName("BikeShare")
        .setInstructions(INSTRUCTIONS)
        // Tools are discovered from the app's AppFunctions (listNetworks, listStations,
        // findNearbyStations); nothing is declared twice.
        .enableAppFunctions(true)
        .setMaxTurns(8)
        .build()

    // Created on first use, after the model is known to be available. The session keeps the
    // conversation history; living in the ViewModel, it survives rotation.
    private var runner: AgentRunner? = null
    private var session: AgentSession? = null

    init {
        if (Build.VERSION.SDK_INT < 36) {
            unsupported("AppFunctions need Android 16 (API 36); this is API ${Build.VERSION.SDK_INT}")
        } else {
            viewModelScope.launch { prepareModel() }
        }
    }

    private suspend fun prepareModel() {
        // Everything ML Kit, getClient() included, stays inside the try: this runs at app start,
        // and a failure here must hide "Ask", never crash the app (1.0.17 did, when R8 broke
        // ML Kit's initialisation).
        var model: GenerativeModel? = null
        try {
            model = Generation.getClient()
            when (val status = model.checkStatus()) {
                FeatureStatus.AVAILABLE -> _availability.value = Availability.Ready
                FeatureStatus.DOWNLOADABLE, FeatureStatus.DOWNLOADING -> {
                    _availability.value = Availability.Downloading(null)
                    model.download().collect { download ->
                        when (download) {
                            is DownloadStatus.DownloadStarted -> _availability.value = Availability.Downloading(null)
                            is DownloadStatus.DownloadProgress ->
                                _availability.value = Availability.Downloading(download.totalBytesDownloaded)
                            DownloadStatus.DownloadCompleted -> _availability.value = Availability.Ready
                            is DownloadStatus.DownloadFailed ->
                                unsupported("Gemini Nano download failed: ${download.e.message}")
                        }
                    }
                }
                else -> unsupported(
                    "Gemini Nano isn't available here (status $status). The agent API needs a supported " +
                        "device and a Google account in the AICore Developer Preview."
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            unsupported("Couldn't check for Gemini Nano: ${e.message}")
        } finally {
            model?.close()
        }
    }

    fun ask(question: String) {
        val text = question.trim()
        if (text.isEmpty() || _busy.value || _availability.value != Availability.Ready) return
        _busy.value = true
        _messages.update { it + ChatMessage.User(text) + ChatMessage.Agent() }
        viewModelScope.launch {
            try {
                val session = session ?: AgentRunner(getApplication(), agent).let { newRunner ->
                    runner = newRunner
                    newRunner.getOrCreateSession(SESSION_ID).also { session = it }
                }
                session.runAsync(Content.builder().text(text).build()).collect { event ->
                    _messages.update { it.withAgentEvent(event) }
                }
                _messages.update { it.finishAgentReply(error = null) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Agent run failed", e)
                _messages.update { it.finishAgentReply(error = e.message ?: e.javaClass.simpleName) }
            } finally {
                _busy.value = false
            }
        }
    }

    /** Forgets the conversation; the next question starts a fresh session. */
    fun clear() {
        if (_busy.value) return
        val current = session
        session = null
        _messages.value = emptyList()
        viewModelScope.launch { runCatching { current?.delete() } }
    }

    private fun unsupported(reason: String) {
        Log.i(TAG, "Ask BikeShare unavailable: $reason")
        _availability.value = Availability.Unsupported(reason)
    }

    override fun onCleared() {
        session?.close()
        runner?.close()
    }

    private companion object {
        const val TAG = "BikeShareAgent"
        const val SESSION_ID = "ask-bikeshare"
        val INSTRUCTIONS = """
            You help people find shared bikes and free docks using BikeShare's tools.
            Never invent networks, stations or numbers: look them up with the tools.
            To answer about a city, first find its network ID with listNetworks, then use
            listStations or findNearbyStations with that ID.
            Keep answers short: name the stations and say how many bikes and free docks each has.
            For the stations with the most free bikes or empty docks, call listStations with
            sortBy ("freeBikes" or "emptyDocks") and limit 5, and keep the order it returns.
            For stations near a named place, call findNearbyStations with that place's name as
            place. Never guess latitude and longitude.
        """.trimIndent()
    }
}

/** One entry in the conversation. */
sealed interface ChatMessage {
    data class User(val text: String) : ChatMessage

    data class Agent(
        val text: String = "",
        /** Tool calls made while answering, e.g. `listStations(networkId=dublinbikes)`. */
        val toolCalls: List<String> = emptyList(),
        val inProgress: Boolean = true,
        val error: String? = null,
    ) : ChatMessage
}

/**
 * Folds one [AgentEvent] into the reply being built (the last message). Partial text may arrive as
 * deltas or as the text so far, so a partial that extends what we have replaces it, any other is
 * appended; a complete message replaces the partials before it.
 */
internal fun List<ChatMessage>.withAgentEvent(event: AgentEvent): List<ChatMessage> {
    val reply = lastOrNull() as? ChatMessage.Agent ?: return this
    val updated = when (event) {
        is AgentEvent.FunctionCall -> reply.copy(toolCalls = reply.toolCalls + event.describe())
        is AgentEvent.TextMessage -> reply.copy(
            text = when {
                !event.isPartial -> event.text
                event.text.startsWith(reply.text) -> event.text
                else -> reply.text + event.text
            }
        )
        else -> reply // FunctionResponse: the model reads it; nothing new to show
    }
    return dropLast(1) + updated
}

internal fun List<ChatMessage>.finishAgentReply(error: String?): List<ChatMessage> {
    val reply = lastOrNull() as? ChatMessage.Agent ?: return this
    return dropLast(1) + reply.copy(text = reply.text.trim(), inProgress = false, error = error)
}

private fun AgentEvent.FunctionCall.describe(): String {
    // AppFunction ids look like "package.Class#function"; the function name is enough to show.
    val function = name.substringAfterLast('#').substringAfterLast('.')
    val args = args.entries.filter { it.value != null }.joinToString { "${it.key}=${it.value}" }
    return "$function($args)"
}
