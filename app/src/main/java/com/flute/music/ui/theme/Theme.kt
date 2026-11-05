package com.flute.music.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FluteDarkColorScheme = darkColorScheme(
    primary = FlutePrimary,
    onPrimary = FluteOnPrimary,
    primaryContainer = FlutePrimaryContainer,
    onPrimaryContainer = FluteOnPrimaryContainer,
    secondary = FluteSecondary,
    onSecondary = FluteOnPrimary,
    secondaryContainer = FlutePrimaryContainer,
    onSecondaryContainer = FluteOnPrimaryContainer,
    tertiary = FluteHeart,
    onTertiary = FluteOnPrimary,
    background = FluteBackground,
    onBackground = FluteOnBackground,
    surface = FluteSurface,
    onSurface = FluteOnSurface,
    surfaceVariant = FluteSurfaceVariant,
    onSurfaceVariant = FluteOnSurfaceVariant,
    error = FluteError,
    onError = FluteOnPrimary,
    outline = FluteOutline,
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black
)

@Composable
fun FluteTheme(content: @Composable () -> Unit) {
    val colorScheme = FluteDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = FluteBackground.toArgb()
                window.navigationBarColor = FluteBackground.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FluteTypography,
        content = content
    )
}
