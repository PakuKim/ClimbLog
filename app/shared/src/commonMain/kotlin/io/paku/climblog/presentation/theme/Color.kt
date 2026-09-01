package io.paku.climblog.presentation.theme

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object AppColors {
    val charcoal900 = Color(0xFF1B1C1E)
    val charcoal800 = Color(0xFF272320)
    val white = Color(0xFFFFFFFF)
    val slate700 = Color(0xFF464646)
    val slate600 = Color(0xFF6B6B6B)
    val slate500 = Color(0xFF909090)
    val slate400 = Color(0xFFB5B5B5)
    val slate300 = Color(0xFFDADADA)
    val slate200 = Color(0xFFEEEEEE)
    val slate100 = Color(0xFFF8F8F8)
    val slate50 = Color(0xFFF1F3F4)
    val teal500 = Color(0xFF0096AA)
    val teal400 = Color(0xFF37C3D6)
    val teal200 = Color(0xFFAFE7EF)
    val teal100 = Color(0xFFD7F3F7)
    val teal50 = Color(0xFFEBF9FB)
    val indigo900 = Color(0xFF032974)
    val blue500 = Color(0xFF32A6EB)
    val blue400 = Color(0xFF5BB8EF)
    val coral500 = Color(0xFFFF4747)
    val coral700 = Color(0xFFCA3D49)
    val magenta500 = Color(0xFFC62F79)
    val red500 = Color(0xFFF75640)
    val red200 = Color(0xFFF9BAB5)
    val orange400 = Color(0xFFffa726)
    val borderColor = Color(0xFFDBDBDC)
}

/**
 * AppComponentColors provides pre-configured color schemes for common Material3 components,
 * ensuring design consistency across the ClimbLog app (Instagram-inspired).
 */
object AppComponentColors {

    @Composable
    fun primaryButtonColors() = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
    )

    @Composable
    fun secondaryButtonColors() = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    )

    @Composable
    fun textButtonColors() = ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    )

    @Composable
    fun filledTextFieldColors() = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        disabledContainerColor = MaterialTheme.colorScheme.surface,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        errorIndicatorColor = MaterialTheme.colorScheme.error,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )

    @Composable
    fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )

    @Composable
    fun searchTextFieldColors() = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        cursorColor = MaterialTheme.colorScheme.primary
    )

    @Composable
    fun cardColors() = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
    )

    @Composable
    fun navigationBarColors() = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.onSurface,
        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        selectedTextColor = MaterialTheme.colorScheme.onSurface,
        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        indicatorColor = Color.Transparent
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun topAppBarColors() = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface
    )

    @Composable
    fun checkboxColors() = CheckboxDefaults.colors(
        checkedColor = MaterialTheme.colorScheme.primary,
        uncheckedColor = MaterialTheme.colorScheme.outline,
        checkmarkColor = MaterialTheme.colorScheme.onPrimary
    )
}
