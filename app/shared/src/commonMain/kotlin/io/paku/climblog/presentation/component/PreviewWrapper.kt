package io.paku.climblog.presentation.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.paku.climblog.presentation.theme.AppTheme

@Composable
fun PreviewWrapper(content: @Composable () -> Unit) {
    AppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            content()
        }
    }
}