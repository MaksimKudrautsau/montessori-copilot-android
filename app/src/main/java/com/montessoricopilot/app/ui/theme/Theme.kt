package com.montessoricopilot.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Terracotta,
    onPrimary = Cream,
    secondary = Sage,
    onSecondary = Cream,
    background = Cream,
    onBackground = WalnutBrown,
    surface = SandBeige,
    onSurface = WalnutBrown,
    tertiary = SoftClay,
)

private val DarkColors = darkColorScheme(
    primary = SoftClay,
    onPrimary = WalnutBrown,
    secondary = Sage,
    onSecondary = WalnutBrown,
    background = WalnutBrown,
    onBackground = Cream,
    surface = DarkSurface,
    onSurface = Cream,
    tertiary = Terracotta,
)

@Composable
fun MontessoriCopilotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MontessoriTypography,
        content = content,
    )
}
