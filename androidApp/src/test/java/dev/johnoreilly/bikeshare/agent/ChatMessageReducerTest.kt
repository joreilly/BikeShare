package dev.johnoreilly.bikeshare.agent

import com.google.mlkit.genai.agent.AgentEvent
import org.junit.Assert.assertEquals
import org.junit.Test

class ChatMessageReducerTest {

    private val start = listOf(ChatMessage.User("Any bikes?"), ChatMessage.Agent())

    private fun List<ChatMessage>.reply() = last() as ChatMessage.Agent

    @Test
    fun toolCallsAreListedByFunctionName() {
        val after = start.withAgentEvent(
            AgentEvent.FunctionCall(
                "dev.johnoreilly.bikeshare#listStations",
                mapOf("networkId" to "dublinbikes", "query" to null),
            )
        )
        assertEquals(listOf("listStations(networkId=dublinbikes)"), after.reply().toolCalls)
    }

    @Test
    fun partialTextWorksAsDeltasOrAsTextSoFar() {
        val deltas = start
            .withAgentEvent(AgentEvent.TextMessage("Three ", true))
            .withAgentEvent(AgentEvent.TextMessage("stations", true))
        assertEquals("Three stations", deltas.reply().text)

        val cumulative = start
            .withAgentEvent(AgentEvent.TextMessage("Three", true))
            .withAgentEvent(AgentEvent.TextMessage("Three stations", true))
        assertEquals("Three stations", cumulative.reply().text)
    }

    @Test
    fun completeMessageReplacesPartialsAndFinishingTrims() {
        val after = start
            .withAgentEvent(AgentEvent.TextMessage("Thr", true))
            .withAgentEvent(AgentEvent.TextMessage("Three stations have bikes. ", false))
            .finishAgentReply(error = null)
        assertEquals(ChatMessage.Agent(text = "Three stations have bikes.", inProgress = false), after.reply())
        assertEquals(ChatMessage.User("Any bikes?"), after.first())
    }

    @Test
    fun eventsWithoutAReplyInProgressAreIgnored() {
        val onlyUser = listOf(ChatMessage.User("hi"))
        assertEquals(onlyUser, onlyUser.withAgentEvent(AgentEvent.TextMessage("x", false)))
    }
}
