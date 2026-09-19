package io.paku.climblog.presentation.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.paku.climblog.presentation.ext.multipleEventsCutter
import io.paku.climblog.presentation.theme.AppComponentColors

@Composable
fun SharedButton(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    titleTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: ButtonColors = AppComponentColors.primaryButtonColors(),
    contentPadding: PaddingValues = PaddingValues(
        vertical = 16.dp
    )
) {
    multipleEventsCutter {
        Button(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled,
            shape = shape,
            contentPadding = contentPadding,
            colors = colors
        ) {
            Text(
                text = title,
                style = titleTextStyle,
            )
        }
    }
}

@Preview
@Composable
private fun SharedButtonPreview() {
    PreviewWrapper {
        SharedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            title = "버튼",
            onClick = {}
        )
    }
}