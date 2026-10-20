package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GardenColorScheme = lightColorScheme(
    primary = GardenGreenPrimary,
    onPrimary = GardenOnPrimary,
    primaryContainer = GardenGreenContainer,
    onPrimaryContainer = GardenGreenPrimary,
    secondary = SproutLime,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCEDC8),
    tertiary = StarGold,
    onTertiary = Color(0xFF3E2723),
    background = SkyBlueBackground,
    onBackground = TextDark,
    surface = CardBackgroundLight,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFE8F5E9),
    onSurfaceVariant = TextMuted
)

private val DarkGardenColorScheme = darkColorScheme(
    primary = GardenGreenLight,
    onPrimary = Color.Black,
    primaryContainer = GardenGreenPrimary,
    onPrimaryContainer = Color.White,
    secondary = SproutLime,
    onSecondary = Color.Black,
    tertiary = StarGoldLight,
    background = Color(0xFF132215),
    onBackground = Color(0xFFE8F5E9),
    surface = Color(0xFF1E3321),
    onSurface = Color(0xFFE8F5E9)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkGardenColorScheme else GardenColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
