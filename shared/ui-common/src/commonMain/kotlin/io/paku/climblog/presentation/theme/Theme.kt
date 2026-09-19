package io.paku.climblog.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val darkColorPalette = darkColorScheme(
    primary = AppColors.slate600, // Medium-light grey for buttons
    onPrimary = AppColors.white,
    primaryContainer = AppColors.slate700,
    onPrimaryContainer = AppColors.white,
    secondary = AppColors.slate700,
    onSecondary = AppColors.white,
    background = AppColors.pureBlack,
    onBackground = AppColors.white,
    surface = AppColors.charcoal900, // Slightly brighter than background
    onSurface = AppColors.white, // Primary text
    surfaceVariant = AppColors.charcoal800, // For secondary backgrounds like cards
    onSurfaceVariant = AppColors.slate400, // Secondary text (light grey)
    error = AppColors.red500,
    onError = AppColors.white,
    outline = AppColors.darkBorder, // Border color
    outlineVariant = AppColors.slate700 // Disabled text/elements (dark grey)
)

val lightColorPalette = lightColorScheme(
    primary = AppColors.blue300, // Instagram blue for light mode
    onPrimary = AppColors.white,
    primaryContainer = AppColors.blue400,
    onPrimaryContainer = AppColors.white,
    secondary = AppColors.slate200,
    onSecondary = AppColors.charcoal900,
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
    val colors = if (!darkTheme) {
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
