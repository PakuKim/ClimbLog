package io.paku.climblog.presentation.ui.main.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.paku.climblog.business.domain.model.Notification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    onNavigateBack: () -> Unit,
    onUserClick: (Long) -> Unit,
    onVideoClick: (Long) -> Unit
) {
    val state = viewModel.state.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("알림", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                if (viewModel.isLoading.value) {
                    CircularProgressIndicator()
                } else {
                    Text("알림이 없습니다.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(state.notifications) { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = {
                            if (notification.videoId != null) {
                                onVideoClick(notification.videoId)
                            } else {
                                onUserClick(notification.fromUserId)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // From User Profile Image
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            val message = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(notification.fromUserName)
                }
                append(" ")
                append(
                    when (notification.type) {
                        "LIKE" -> "님이 회원님의 영상을 좋아합니다."
                        "COMMENT" -> "님이 댓글을 남겼습니다."
                        "FOLLOW" -> "님이 회원님을 팔로우하기 시작했습니다."
                        else -> "님이 상호작용했습니다."
                    }
                )
            }
            
            Text(text = message, fontSize = 14.sp, lineHeight = 18.sp)
            Text(
                text = "방금 전", // Simplified
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        
        if (notification.videoId != null) {
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.DarkGray)
            )
        }
    }
}
