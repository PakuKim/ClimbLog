package io.paku.climblog.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.paku.climblog.business.domain.model.video.Video
import io.paku.climblog.business.domain.model.video.VideoStatus
import io.paku.climblog.core.VideoPlayerView
import io.paku.climblog.core.rememberVideoPlayerController
import io.paku.climblog.presentation.ext.noRippleClickable
import kotlinx.datetime.LocalDateTime

@Composable
fun SharedVideoItem(
    video: Video,
    isCurrent: Boolean,
    isLiked: Boolean,
    isExpanded: Boolean = false,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onUserClick: ((Long) -> Unit)? = null
) {
    val controller = rememberVideoPlayerController(video.hlsUrl)
    var isPausedInternal by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }

    LaunchedEffect(isCurrent, isExpanded, video.status) {
        if (isCurrent && !isExpanded && video.status == VideoStatus.READY) {
            controller.play()
            isPausedInternal = false
        } else {
            controller.pause()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        VideoPlayerView(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable {
                    if (video.status == VideoStatus.READY) {
                        if (isPausedInternal) controller.play() else controller.pause()
                        isPausedInternal = !isPausedInternal
                    }
                },
            controller = controller,
        )

        if (video.status != VideoStatus.READY) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = when(video.status) {
                            VideoStatus.UPLOADING -> "업로드 중..."
                            VideoStatus.PROCESSING -> "영상 처리 중..."
                            VideoStatus.FAILED -> "처리 실패"
                            VideoStatus.READY -> ""
                        },
                        color = Color.White
                    )
                }
            }
        }

        if (!isExpanded) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 120.dp)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.End),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    InteractionIcon(
                        icon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        color = if (isLiked) Color.Red else Color.White,
                        onClick = onLikeClick
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InteractionIcon(
                        icon = Icons.Outlined.ChatBubbleOutline,
                        onClick = onCommentClick
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InteractionIcon(
                        icon = Icons.Default.Share,
                        onClick = onShareClick
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                    ) {
                        Text(
                            text = video.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        video.description?.let {
                            Text(
                                text = it,
                                color = Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        if (video.cruxes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))

                            SuggestionChip(
                                onClick = {
                                    video.cruxes.firstOrNull()?.startTime?.let {
                                        controller.seekTo((it * 1000).toLong())
                                        controller.play()
                                        isPausedInternal = false
                                    }
                                },
                                label = { Text("Crux Section", color = Color.White) },
                                shape = RoundedCornerShape(16.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = Color.White.copy(alpha = 0.2f)
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                    ) {
                        var showSpeedMenu by remember { mutableStateOf(false) }

                        TextButton(
                            onClick = { showSpeedMenu = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Text("${playbackSpeed}x", fontWeight = FontWeight.Bold)
                        }

                        DropdownMenu(
                            expanded = showSpeedMenu,
                            onDismissRequest = { showSpeedMenu = false }
                        ) {
                            listOf(0.5f, 0.8f, 1.0f, 1.2f, 1.5f, 2.0f).forEach { speed ->
                                DropdownMenuItem(
                                    text = { Text("${speed}x") },
                                    onClick = {
                                        playbackSpeed = speed
                                        controller.setPlaybackSpeed(speed)
                                        showSpeedMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                HorizontalDivider(
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun InteractionIcon(
    icon: ImageVector,
    color: Color = Color.White,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Preview
@Composable
private fun SharedVideoItemPreview() {
    PreviewWrapper {
        SharedVideoItem(
            video = Video(
                id = 1,
                userId = 1,
                title = "Sample Video",
                description = "This is a sample video description.",
                hlsUrl = "https://example.com/video.m3u8",
                thumbnailUrl = "https://example.com/thumbnail.jpg",
                cruxes = emptyList(),
                createdAt = LocalDateTime(2023, 1, 1, 0, 0)
            ),
            isCurrent = true,
            isLiked = false,
            onLikeClick = {},
            onCommentClick = {},
            onShareClick = {}
        )
    }
}