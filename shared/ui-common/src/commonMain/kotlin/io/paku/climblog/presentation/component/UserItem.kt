package io.paku.climblog.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.paku.climblog.business.domain.model.user.User

@Composable
fun UserItem(
    user: User,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image Placeholder
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        // TODO: Use Coil to load user.profilePhotoUrl
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = user.handle.ifBlank { user.name },
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = user.name,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
private fun UserItemPreview() {
    PreviewWrapper {
        UserItem(
            user = User(
                id = 1,
                name = "홍길동",
                handle = "gildong",
                age = 20,
                height = 180,
                armReach = 190,
                gender = "M",
                profilePhotoUrl = null
            ),
            onClick = {}
        )
    }
}
