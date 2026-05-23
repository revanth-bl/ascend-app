package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberColorScheme = darkColorScheme(
    primary = CyberPurple,
    onPrimary = Color.Black,
    secondary = CyberCyan,
    onSecondary = Color.Black,
    tertiary = CyberPink,
    onTertiary = Color.White,
    background = CyberBlack,
    onBackground = TextPrimary,
    surface = CyberNavy,
    onSurface = TextPrimary,
    error = WarningRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force cyberpunk dark mode always!
    dynamicColor: Boolean = false, // Disable dynamic color to maintain tactical cyber branding!
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}
