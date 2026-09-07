package io.paku.climblog.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.paku.climblog.business.domain.model.Comment
import io.paku.climblog.presentation.theme.AppComponentColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SharedCommentLayout(
    comments: List<Comment>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onPostComment: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.titleMedium,
            text = "댓글",
        )

        HorizontalDivider()

        Box(
            modifier = Modifier.weight(1f)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(comments) { comment ->
                        CommentItem(comment)
                    }
                }
            }
        }

        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("댓글 달기...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = AppComponentColors.outlinedTextFieldColors()
            )
            TextButton(
                onClick = {
                    if (commentText.isNotBlank()) {
                        onPostComment(commentText)
                        commentText = ""
                    }
                },
                enabled = commentText.isNotBlank(),
                colors = AppComponentColors.textButtonColors()
            ) {
                Text("게시")
            }
        }
    }
}

@Composable
private fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(comment.userName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(comment.content, fontSize = 14.sp)
            Text("방금 전", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
