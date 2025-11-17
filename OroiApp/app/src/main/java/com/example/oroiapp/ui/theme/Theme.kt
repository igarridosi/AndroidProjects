package com.example.oroiapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Kolore paleta ilunerako
private val DarkColorScheme = darkColorScheme(
    primary = OroiBrightPurple,
    background = Color(0xFF1C1B1F),
    surface = OroiDeepPurple,
    surfaceVariant = OroiLavender,
    onPrimary = Color.Black,
    onBackground = OroiBrightPurple,
    onSurface = OroiTextOnDeepPurple,
    onSurfaceVariant = OroiTextOnLavender
)

private val LightColorScheme = lightColorScheme(
    primary = OroiBrightPurple,
    background = OroiBackground,
    surface = OroiDeepPurple,
    surfaceVariant = OroiLavender,
    onPrimary = Color.Black,
    onBackground = OroiBrightPurple,
    onSurface = OroiTextOnDeepPurple,
    onSurfaceVariant = OroiTextOnLavender
)


@Composable
fun OroiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 'Dynamic Color' Android 12+ bertsioetan bakarrik dago eskuragarri
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = OroiBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}