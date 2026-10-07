package io.paku.climblog.presentation.ui.main.video

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import io.paku.climblog.navigation.VideoListType
import io.paku.climblog.platform.shareLink
import io.paku.climblog.presentation.component.SharedCommentLayout
import io.paku.climblog.presentation.component.SharedVideoItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun VideoRoute(
    videoListType: VideoListType,
    initialVideoId: Long? = null,
    isNavigationVisible: Boolean = true,
    onBackClick: (() -> Unit)? = null,
    onUserClick: ((Long) -> Unit)? = null,
    onToggleBottomNavClick: ((Boolean) -> Unit)? = null,
    viewModel: SharedVideoViewModel = koinViewModel()
) {
    val state by viewModel.state

    LaunchedEffect(videoListType) {
        viewModel.onEvent(SharedVideoViewModelEvent.Init(videoListType, initialVideoId))
    }

    SharedVideoContent(
        state = state,
        onLikeClick = { videoId -> viewModel.onEvent(SharedVideoViewModelEvent.OnLikeClick(videoId)) },
        onCommentClick = { videoId -> viewModel.onEvent(SharedVideoViewModelEvent.LoadComments(videoId)) },
        onPostComment = { content -> viewModel.onEvent(SharedVideoViewModelEvent.PostComment(content)) },
        onSheetStateChange = { isVisible -> 
            onToggleBottomNavClick?.invoke(!isVisible)
        },
        onBackClick = onBackClick,
        onUserClick = onUserClick,
        isNavigationVisible = isNavigationVisible
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SharedVideoContent(
    state: SharedVideoViewModelState,
    onLikeClick: (Long) -> Unit,
    onCommentClick: (Long) -> Unit,
    onPostComment: (String) -> Unit,
    onSheetStateChange: (Boolean) -> Unit,
    onBackClick: (() -> Unit)? = null,
    onUserClick: ((Long) -> Unit)? = null,
    isNavigationVisible: Boolean = true
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val fullHeight = constraints.maxHeight.toFloat()
        val videoHeightRatio = 3f / 7f
        val sheetHeightRatio = 4f / 7f

        // Expanded position of the sheet (from top)
        val expandedSheetOffset = fullHeight * videoHeightRatio

        val scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Hidden,
                skipHiddenState = false,
                confirmValueChange = { true }
            )
        )

        // Calculate progress (0.0 when hidden, 1.0 when expanded)
        val progress by remember {
            derivedStateOf {
                val currentOffset = try {
                    scaffoldState.bottomSheetState.requireOffset()
                } catch (_: Exception) {
                    fullHeight
                }
                ((fullHeight - currentOffset) / (fullHeight - expandedSheetOffset)).coerceIn(0f, 1f)
            }
        }

        val pagingVideoItems = state.videoPagingData?.collectAsLazyPagingItems()
        val commentPagingItems = state.commentsPagingData?.collectAsLazyPagingItems()
        val pagerState = rememberPagerState { pagingVideoItems?.itemCount ?: 0 }

        var showCommentsForVideoId by remember { mutableStateOf<Long?>(null) }

        val isVisible = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded

        LaunchedEffect(showCommentsForVideoId) {
            if (showCommentsForVideoId != null) {
                onSheetStateChange(true)
                scaffoldState.bottomSheetState.expand()
            } else {
                onSheetStateChange(false)
                scaffoldState.bottomSheetState.hide()
            }
        }

        LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
//            onSheetStateChange(isVisible)
            if (!isVisible) {
                showCommentsForVideoId = null
            }
        }

        // Scroll to initial video if provided
        LaunchedEffect(state.initialVideoId, pagingVideoItems?.itemCount) {
            if (state.initialVideoId != null && pagingVideoItems != null) {
                val index = (0 until pagingVideoItems.itemCount).firstOrNull {
                    pagingVideoItems[it]?.id == state.initialVideoId
                }
                if (index != null) {
                    pagerState.scrollToPage(index)
                }
            }
        }

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 0.dp,
            sheetSwipeEnabled = isVisible,
            sheetContent = {
                Box(
                    modifier = Modifier.fillMaxHeight(sheetHeightRatio)
                ) {
                    SharedCommentLayout(
                        comments = commentPagingItems,
                        isPosting = state.isPosting,
                        onDismiss = { showCommentsForVideoId = null },
                        onPostComment = onPostComment
                    )
                }
            }
        ) { _ ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = -(progress * (fullHeight * 0.286f))

                        // Optional: Scale down slightly (to 90%)
                        val scale = 1f - (progress * 0.1f)
                        scaleX = scale
                        scaleY = scale
                    }
            ) {
                VerticalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState,
                    beyondViewportPageCount = 1,
                    userScrollEnabled = !isVisible
                ) { index ->
                    val video = pagingVideoItems?.get(index)
                    if (video != null) {
                        SharedVideoItem(
                            video = video,
                            isCurrent = pagerState.currentPage == index,
                            isLiked = state.likedVideoIds.contains(video.id),
                            isExpanded = progress > 0.5f,
                            onLikeClick = { onLikeClick(video.id) },
                            onCommentClick = {
                                showCommentsForVideoId = video.id
                                onCommentClick(video.id)
                            },
                            onShareClick = { shareLink(video.hlsUrl.orEmpty()) },
                            onUserClick = onUserClick
                        )
                    }
                }
            }
        }
    }
}
