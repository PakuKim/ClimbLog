package io.paku.climblog.presentation.ui.main.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.compose.collectAsLazyPagingItems
import io.paku.climblog.core.shareLink
import io.paku.climblog.presentation.component.PreviewWrapper
import io.paku.climblog.presentation.component.SharedTopAppBar
import io.paku.climblog.presentation.component.SharedVideoItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeRoute(
    viewModel: HomeFeedViewModel = koinViewModel(),
    navigateToUpload: () -> Unit
) {
    val state by viewModel.state

    HomeScreen(
        state = state,
        onVideoLikeClick = { videoId, _ -> 
            viewModel.onEvent(HomeViewModelEvent.OnLikeClick(videoId))
        },
        onVideoCommentClick = { videoId ->
            viewModel.onEvent(HomeViewModelEvent.LoadComments(videoId))
        },
        onPostComment = { videoId, content ->
            viewModel.onEvent(HomeViewModelEvent.PostComment(videoId, content))
        },
        onUploadClick = navigateToUpload,
        onLogoutClick = {
            viewModel.onEvent(HomeViewModelEvent.Logout)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    state: HomeViewModelState,
    onVideoLikeClick: (Long, Boolean) -> Unit = { _, _ -> },
    onVideoCommentClick: (Long) -> Unit = {},
    onPostComment: (Long, String) -> Unit = { _, _ -> },
    onUploadClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val pagingItems = state.videoPagingData?.collectAsLazyPagingItems()
    val pagerState = rememberPagerState { pagingItems?.itemCount ?: 0 }
    
    var showCommentsForVideoId by remember { mutableStateOf<Long?>(null) }
//    LaunchedEffect(showCommentsForVideoId) {
//        if (showCommentsForVideoId != null) {
//            scaffoldState.bottomSheetState.show()
//        } else {
//            scaffoldState.bottomSheetState.hide()
//        }
//    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            SharedTopAppBar(
                title = "홈",
                actions = {
                    Row {
                        IconButton(
                            onClick = onLogoutClick
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout"
                            )
                        }

                        IconButton(
                            onClick = onUploadClick,
                        ) {
                            Icon(
                                Icons.Default.CloudUpload,
                                contentDescription = "Upload Video"
                            )
                        }
                    }
                }
            )
        },
        sheetContent = {
//            SharedCommentLayout(
//                comments = state.commentList,
//                isLoading = state.isCommentsLoading,
//                onDismiss = { showCommentsForVideoId = null },
//                onPostComment = { content ->
//                    showCommentsForVideoId?.let {
//                        onPostComment(it, content)
//                    }
//                }
//            )
        }
    ) { paddingValues ->
        VerticalPager(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = pagerState,
            beyondViewportPageCount = 1
        ) { index ->
            val video = pagingItems?.get(index)
            if (video != null) {
                SharedVideoItem(
                    video = video,
                    isCurrent = pagerState.currentPage == index,
                    isLiked = state.likedVideoIds.contains(video.id),
                    onLikeClick = { onVideoLikeClick(video.id, true) },
                    onCommentClick = {
                        showCommentsForVideoId = video.id
                        onVideoCommentClick(video.id)
                    },
                    onShareClick = { shareLink(video.hlsUrl) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    PreviewWrapper {
        HomeScreen(
            state = HomeViewModelState()
        )
    }
}