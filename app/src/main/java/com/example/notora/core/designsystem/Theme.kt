package com.example.notora.core.designsystem

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalThemeIsDark = compositionLocalOf { false }

private val DarkColorScheme = darkColorScheme(
    primary = NotoraPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = NotoraPrimaryContainerDark,
    onPrimaryContainer = NotoraOnPrimaryContainerDark,
    secondary = NotoraCyanDark,
    onSecondary = DarkBackground,
    secondaryContainer = NotoraSecondaryContainerDark,
    onSecondaryContainer = NotoraOnSecondaryContainerDark,
    tertiary = NotoraRoseDark,
    onTertiary = DarkBackground,
    tertiaryContainer = NotoraTertiaryContainerDark,
    onTertiaryContainer = NotoraOnTertiaryContainerDark,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLow = DarkSurfaceLow,
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DarkSurfaceHigh,
    surfaceContainerHighest = DarkSurfaceHighest,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = NotoraError
)

private val LightColorScheme = lightColorScheme(
    primary = NotoraPrimaryLight,
    onPrimary = LightSurface,
    primaryContainer = NotoraPrimaryContainerLight,
    onPrimaryContainer = NotoraOnPrimaryContainerLight,
    secondary = NotoraCyanLight,
    onSecondary = LightSurface,
    secondaryContainer = NotoraSecondaryContainerLight,
    onSecondaryContainer = NotoraOnSecondaryContainerLight,
    tertiary = NotoraRoseLight,
    onTertiary = LightSurface,
    tertiaryContainer = NotoraTertiaryContainerLight,
    onTertiaryContainer = NotoraOnTertiaryContainerLight,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainerLow = LightSurfaceLow,
    surfaceContainer = LightSurfaceVariant,
    surfaceContainerHigh = LightSurfaceHigh,
    surfaceContainerHighest = LightSurfaceHighest,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    error = NotoraError
)

/**
 * Central Notora Theme Manager.
 * Guarantees 100% consistent colors across all Android versions and devices.
 */
@Composable
fun NotoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Enforce Notora's custom branded palette consistently across devices
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val windowInsetsController = WindowCompat.getInsetsController(window, view)
                windowInsetsController.isAppearanceLightStatusBars = !darkTheme
                windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalThemeIsDark provides darkTheme,
        LocalDimensions provides resolveDimensions()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography.toMaterial3Typography(),
            content = content
        )
    }
}
