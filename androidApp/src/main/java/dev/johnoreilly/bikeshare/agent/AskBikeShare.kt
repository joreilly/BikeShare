package dev.johnoreilly.bikeshare.agent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikepenz.markdown.m3.Markdown

/**
 * Lays an "Ask" button over [content] that opens a chat with the on-device agent. Shows nothing
 * extra until the agent is known to work on this device.
 */
@Composable
fun AskBikeShareOverlay(viewModel: BikeShareAgentViewModel, content: @Composable () -> Unit) {
    val availability by viewModel.availability.collectAsStateWithLifecycle()
    var open by rememberSaveable { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        content()
        // Only once the model is known to be usable, so the button doesn't flash up and vanish
        // on the many devices where it isn't.
        if (availability == BikeShareAgentViewModel.Availability.Ready ||
            availability is BikeShareAgentViewModel.Availability.Downloading
        ) {
            ExtendedFloatingActionButton(
                onClick = { open = true },
                icon = { Icon(Icons.Filled.AutoAwesome, contentDescription = null) },
                text = { Text("Ask") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(16.dp),
            )
        }
    }
    if (open) AskBikeShareSheet(viewModel, onDismiss = { open = false })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AskBikeShareSheet(viewModel: BikeShareAgentViewModel, onDismiss: () -> Unit) {
    val availability by viewModel.availability.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val busy by viewModel.busy.collectAsStateWithLifecycle()
    var input by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, (messages.lastOrNull() as? ChatMessage.Agent)?.text?.length) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxHeight(0.85f).imePadding().padding(horizontal = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Ask BikeShare", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Runs on device with Gemini Nano, using BikeShare's AppFunctions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = viewModel::clear, enabled = messages.isNotEmpty() && !busy) {
                    Icon(Icons.Filled.DeleteSweep, contentDescription = "Clear conversation")
                }
            }

            when (val a = availability) {
                BikeShareAgentViewModel.Availability.Checking ->
                    Status("Checking for Gemini Nano…", progress = true)
                is BikeShareAgentViewModel.Availability.Downloading ->
                    Status("Downloading Gemini Nano" + (a.bytes?.let { " (${it / 1_000_000} MB)" } ?: "") + "…", progress = true)
                is BikeShareAgentViewModel.Availability.Unsupported -> Status(a.reason, progress = false)
                BikeShareAgentViewModel.Availability.Ready -> Unit
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (messages.isEmpty() && availability == BikeShareAgentViewModel.Availability.Ready) {
                    item { Suggestions(onPick = viewModel::ask) }
                }
                items(messages) { message ->
                    when (message) {
                        is ChatMessage.User -> UserBubble(message)
                        is ChatMessage.Agent -> AgentReply(message)
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp).navigationBarsPadding(),
            ) {
                val send = {
                    viewModel.ask(input)
                    input = ""
                }
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Any free bikes near Galway Cathedral?") },
                    enabled = availability == BikeShareAgentViewModel.Availability.Ready,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { send() }),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = send,
                    enabled = input.isNotBlank() && !busy && availability == BikeShareAgentViewModel.Availability.Ready,
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
private fun Status(text: String, progress: Boolean) {
    Column(Modifier.padding(vertical = 12.dp)) {
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (progress) LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 8.dp))
    }
}

@Composable
private fun Suggestions(onPick: (String) -> Unit) {
    Column(Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Try", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        listOf(
            "Which bike share networks are there in Ireland?",
            "Which Dublin Bikes stations have the most free bikes?",
            "Any free docks near Trinity College Dublin?",
        ).forEach { suggestion ->
            AssistChip(onClick = { onPick(suggestion) }, label = { Text(suggestion) })
        }
    }
}

@Composable
private fun UserBubble(message: ChatMessage.User) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp),
            modifier = Modifier.widthIn(max = 300.dp),
        ) {
            Text(message.text, Modifier.padding(horizontal = 14.dp, vertical = 10.dp))
        }
    }
}

@Composable
private fun AgentReply(message: ChatMessage.Agent) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // Which AppFunctions the agent called: shows the tools are doing the work, not the model's memory.
        message.toolCalls.forEach { call ->
            AssistChip(
                onClick = {},
                label = { Text(call, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                leadingIcon = { Icon(Icons.Filled.Build, contentDescription = null, Modifier.size(AssistChipDefaults.IconSize)) },
                modifier = Modifier.semantics { contentDescription = "Called $call" },
            )
        }
        when {
            // Gemini Nano answers in Markdown (bold names, numbered and bulleted lists)
            message.text.isNotEmpty() -> Markdown(message.text)
            message.inProgress -> CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
        }
        message.error?.let {
            Text("Something went wrong: $it", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}
