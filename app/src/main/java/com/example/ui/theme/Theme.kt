package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DijlahDarkColorScheme = darkColorScheme(
    primary = DijlahGold,
    onPrimary = Color(0xFF050B14),
    primaryContainer = DijlahSurfaceElevated,
    onPrimaryContainer = DijlahAmberLight,
    secondary = DijlahCyan,
    onSecondary = Color(0xFF050B14),
    tertiary = DijlahEmerald,
    background = DijlahBgDark,
    onBackground = DijlahTextPrimary,
    surface = DijlahSurfaceCard,
    onSurface = DijlahTextPrimary,
    surfaceVariant = DijlahSurfaceElevated,
    onSurfaceVariant = DijlahTextSecondary,
    error = DijlahCrimson,
    outline = DijlahBorderSubtle
)

@Composable
fun DijlahPlusTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DijlahDarkColorScheme,
        typography = Typography,
        content = content
    )
}
