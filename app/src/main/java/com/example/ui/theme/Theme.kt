package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BedSpaceLightColorScheme = lightColorScheme(
    primary = Navy800,
    onPrimary = White,
    primaryContainer = Blue100,
    onPrimaryContainer = Navy900,
    secondary = Blue600,
    onSecondary = White,
    secondaryContainer = Blue50,
    onSecondaryContainer = Navy800,
    tertiary = Green600,
    onTertiary = White,
    tertiaryContainer = Green100,
    onTertiaryContainer = Green700,
    background = Slate50,
    onBackground = Navy900,
    surface = White,
    onSurface = Slate800,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate500,
    outline = Slate200,
    outlineVariant = Slate300,
    error = Red600,
    onError = White,
    errorContainer = Red100,
    onErrorContainer = Red700
)

private val BedSpaceDarkColorScheme = darkColorScheme(
    primary = Blue500,
    onPrimary = Navy900,
    primaryContainer = Navy800,
    onPrimaryContainer = Blue100,
    secondary = Blue500,
    onSecondary = White,
    background = Color(0xFF0B1120),
    surface = Color(0xFF1E293B),
    onBackground = Slate50,
    onSurface = Slate100,
    outline = Color(0xFF334155),
    error = Red500
)

@Composable
fun BedSpaceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // For crisp branded marketplace look, use our curated high-contrast clean theme
    val colorScheme = if (darkTheme) BedSpaceDarkColorScheme else BedSpaceLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
