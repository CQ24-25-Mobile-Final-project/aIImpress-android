package com.hcmus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),

    onPrimary = BluePrimary,
    onSecondary = BluePrimary,
    onTertiary = BluePrimary,
    onBackground = BluePrimary,
    onSurface = BluePrimary,
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),

    onPrimary = BluePrimary,
    onSecondary = BluePrimary,
    onTertiary = BluePrimary,
    onBackground = BluePrimary,
    onSurface = BluePrimary,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)
@Composable
fun PhotoappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme // Use DarkColorScheme if you have defined it.
    } else {
        LightColorScheme // Use LightColorScheme as default
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

