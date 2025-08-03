package dev.johnoreilly.common.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Window size classes for adaptive layouts
 */
enum class WindowWidthSizeClass {
    Compact,
    Medium,
    Expanded
}

/**
 * Remembers the [WindowWidthSizeClass] based on the window width
 */
@Composable
fun rememberWindowWidthSizeClass(windowWidth: Dp): WindowWidthSizeClass {
    val windowWidthSizeClass = remember(windowWidth) {
        when {
            windowWidth < 600.dp -> WindowWidthSizeClass.Compact
            windowWidth < 840.dp -> WindowWidthSizeClass.Medium
            else -> WindowWidthSizeClass.Expanded
        }
    }
    return windowWidthSizeClass
}

/**
 * Remembers the [WindowWidthSizeClass] based on the window width in pixels
 */
@Composable
fun rememberWindowWidthSizeClass(windowWidthPx: Int): WindowWidthSizeClass {
    val density = LocalDensity.current
    val windowWidth = with(density) { windowWidthPx.toDp() }
    return rememberWindowWidthSizeClass(windowWidth)
}

/**
 * A composable that adapts its content based on the available width
 */
@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    compactContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val windowWidthSizeClass = rememberWindowWidthSizeClass(maxWidth)
        
        when (windowWidthSizeClass) {
            WindowWidthSizeClass.Compact -> compactContent()
            WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> expandedContent()
        }
    }
}