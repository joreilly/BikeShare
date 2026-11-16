package dev.johnoreilly.bikeshare.agent

import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.google.mlkit.genai.agent.AppFunctionAgentTools
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Test

/**
 * ML Kit's agent turns the app's AppFunctions into tools through androidx.appfunctions, which the
 * app supplies. A version it wasn't built against compiles fine but fails here at runtime
 * (NoSuchMethodError), so run this after bumping either library. Needs no Gemini Nano.
 */
class AgentToolDiscoveryTest {

    @Test
    fun agentDiscoversBikeShareAppFunctions() = runBlocking {
        assumeTrue("AppFunctions need Android 16", Build.VERSION.SDK_INT >= 36)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // The String/List overloads take AppFunction ids to enable, not a package name.
        val tools = AppFunctionAgentTools.discover(context)

        assertEquals(
            setOf("listNetworks", "listStations", "findNearbyStations"),
            tools.map { it.name.substringAfterLast('#') }.toSet(),
        )
    }
}
