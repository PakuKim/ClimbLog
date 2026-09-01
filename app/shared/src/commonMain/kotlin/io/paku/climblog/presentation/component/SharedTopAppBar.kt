package io.paku.climblog.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.paku.climblog.presentation.theme.AppComponentColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    titleTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
    onNavClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = titleTextStyle
            )
        },
        navigationIcon = {
            onNavClick?.let {
                Row {
                    Spacer(modifier = Modifier.size(14.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { it() }
                    )
                }
            }
        },
        actions = actions,
        colors = AppComponentColors.topAppBarColors()
    )
}

@Preview(showBackground = true)
@Composable
private fun SharedTopAppBarPreview() {
    PreviewWrapper {
        SharedTopAppBar(
            title = "타이틀",
            onNavClick = {},
            actions = {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "setting")
            }
        )
    }
}
