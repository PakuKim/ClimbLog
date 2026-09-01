package io.paku.climblog.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val darkColorPalette = darkColorScheme(
    primary = AppColors.coral500,
    onPrimary = AppColors.white,
    primaryContainer = AppColors.coral700,
    onPrimaryContainer = AppColors.white,
    secondary = AppColors.magenta500,
    onSecondary = AppColors.white,
    background = AppColors.charcoal900,
    onBackground = AppColors.white,
    surface = AppColors.charcoal900,
    onSurface = AppColors.white,
    error = AppColors.red500,
    onError = AppColors.white,
    outline = AppColors.slate500,
)

val lightColorPalette = lightColorScheme(
    primary = AppColors.coral500,
    onPrimary = AppColors.white,
    primaryContainer = AppColors.coral700,
    onPrimaryContainer = AppColors.white,
    secondary = AppColors.magenta500,
    onSecondary = AppColors.white,
    background = AppColors.white,
    onBackground = AppColors.charcoal900,
    surface = AppColors.slate50,
    onSurface = AppColors.charcoal900,
    surfaceVariant = AppColors.white,
    onSurfaceVariant = AppColors.slate700,
    error = AppColors.red500,
    onError = AppColors.white,
    outline = AppColors.borderColor,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorPalette
    } else {
        lightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = appTypography(),
        shapes = appShapes,
        content = content,
    )
}
