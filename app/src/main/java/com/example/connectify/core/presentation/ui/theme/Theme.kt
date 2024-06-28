package com.example.connectify.core.presentation.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColorScheme(
    primary = GreenAccent,
    background = DarkGray,
    onBackground = TextWhite,
    secondary = MediumGray,
    onSecondary = TextWhite,
    onPrimary = DarkGray,
    surface = MediumGray,
    onSurface = LightGray
)

@Composable
fun ConnectifyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}