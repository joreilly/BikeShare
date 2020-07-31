package com.surrus.bikeshare.ui

import androidx.compose.Composable
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.material.MaterialTheme
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette

private val DarkColorPalette = darkColorPalette(
        primary = maroon200,
        primaryVariant = maroon700,
        secondary = teal200
)

private val LightColorPalette = lightColorPalette(
        primary = maroon500,
        primaryVariant = maroon700,
        secondary = teal200
)

@Composable
fun BikeShareTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
            colors = colors,
            content = content
    )
}