package com.example.connectify.core.presentation.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColorScheme(
    primary = GreenAccent,
    onPrimary = DarkGray,
    background = DarkGray,
    onBackground = TextWhite,
    secondary = MediumGray,
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