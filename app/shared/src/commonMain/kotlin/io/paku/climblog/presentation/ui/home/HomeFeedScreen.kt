package io.paku.climblog.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import io.paku.climblog.business.domain.model.Comment
import io.paku.climblog.business.domain.model.Video
import io.paku.climblog.core.shareLink
import io.paku.climblog.presentation.component.VideoPlayerView
import io.paku.climblog.presentation.component.rememberVideoPlayerController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFeedScreen(
    viewModel: HomeFeedViewModel
) {
    val state = viewModel.state.value
    val pagingItems = state.pagingData?.collectAsLazyPagingItems() ?: return
    val pagerState = rememberPagerState { pagingItems.itemCount }
    
    var showCommentsForVideoId by remember { mutableStateOf<Long?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().background(Color.Black),
            beyondViewportPageCount = 1
        ) { index ->
            val video = pagingItems[index]
            if (video != null) {
                VideoItem(
                    video = video,
                    isCurrent = pagerState.currentPage == index,
                    isLiked = state.likedVideoIds.contains(video.id),
                    onLikeClick = { viewModel.onEvent(HomeFeedEvent.ToggleLike(video.id)) },
                    onCommentClick = {
                        showCommentsForVideoId = video.id
                        viewModel.onEvent(HomeFeedEvent.LoadComments(video.id))
                    },
                    onShareClick = { shareLink(video.hlsUrl) }
                )
            }
        }
        
        if (showCommentsForVideoId != null) {
            CommentsBottomSheet(
                comments = state.commentList,
                isLoading = state.isCommentsLoading,
                onDismiss = { showCommentsForVideoId = null },
                onPostComment = { content ->
                    showCommentsForVideoId?.let { videoId ->
                        viewModel.onEvent(HomeFeedEvent.PostComment(videoId, content))
                    }
                }
            )
        }
    }
}

@Composable
fun VideoItem(
    video: Video,
    isCurrent: Boolean,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val controller = rememberVideoPlayerController(video.hlsUrl)
    var isPaused by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }

    LaunchedEffect(isCurrent) {
        if (isCurrent) {
            controller.play()
            isPaused = false
        } else {
            controller.pause()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        VideoPlayerView(
            controller = controller,
            modifier = Modifier.fillMaxSize().clickable {
                if (isPaused) controller.play() else controller.pause()
                isPaused = !isPaused
            }
        )

        // Right side interactions
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = 120.dp),
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

        // Overlay Info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .padding(bottom = 48.dp)
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
            
            if (video.cruxStartTime != null && video.cruxEndTime != null) {
                Spacer(modifier = Modifier.height(12.dp))
                SuggestionChip(
                    onClick = { 
                        controller.seekTo((video.cruxStartTime * 1000).toLong()) 
                        controller.play()
                        isPaused = false
                    },
                    label = { Text("Crux Section", color = Color.White) },
                    shape = RoundedCornerShape(16.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    )
                )
            }
        }

        // Speed Controller
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 16.dp, vertical = 48.dp)
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
}

@Composable
fun InteractionIcon(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    comments: List<Comment>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onPostComment: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var commentText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "댓글",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 16.dp).align(Alignment.CenterHorizontally)
            )
            
            HorizontalDivider()

            Box(modifier = Modifier.weight(1f)) {
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
                    shape = RoundedCornerShape(24.dp)
                )
                TextButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            onPostComment(commentText)
                            commentText = ""
                        }
                    },
                    enabled = commentText.isNotBlank()
                ) {
                    Text("게시")
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Box(
            modifier = Modifier.size(32.dp).background(Color.LightGray, CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(comment.userName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(comment.content, fontSize = 14.sp)
            Text("방금 전", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
