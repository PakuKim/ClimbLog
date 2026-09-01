package io.paku.climblog.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.paku.climblog.presentation.theme.AppColors

@Composable
fun SharedInputLayout(
    modifier: Modifier = Modifier,
    title: String? = null,
    required: Boolean = false,
    guideText: String? = null,
    isValid: Boolean = false,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        title?.let {
            Text(
                text = buildAnnotatedString {
                    append(title)
                    if (required) {
                        withStyle(
                            SpanStyle(
                                color = AppColors.red500,
                            )
                        ) {
                            append(" *")
                        }
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.charcoal900,
                textAlign = TextAlign.Center
            )
        }

        content()

        if (guideText != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Image(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (isValid) AppColors.slate400 else AppColors.red500)
                        .padding(4.dp),
                    imageVector = if (isValid) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = "SharedInputLayout guide icon",
                )
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = guideText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isValid) AppColors.slate400 else AppColors.red500
                )
            }
        }
    }
}

@Preview
@Composable
private fun SharedInputLayoutPreview() {
    PreviewWrapper {
        SharedInputLayout(
            title = "이메일",
            required = true,
            content = {
                SharedTextField(
                    value = "",
                    onValueChange = {},
                    placeholderText = "이메일을 입력해주세요."
                )
            },
            guideText = "이메일 형식이 올바르지 않습니다.",
            isValid = true
        )
    }
}