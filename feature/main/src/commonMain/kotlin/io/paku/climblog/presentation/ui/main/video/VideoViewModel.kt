package io.paku.climblog.presentation.ui.main.video

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.paku.climblog.navigation.VideoListType
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

data class SharedVideoViewModelState(
    val videoListType: VideoListType? = null,
    val videoPagingData: Flow<PagingData<io.paku.climblog.domain.model.video.Video>>? = null,
    val likedVideoIds: Set<Long> = emptySet(),
    val initialVideoId: Long? = null,
    val currentUserId: Long? = null,
    // Comment related state
    val activeVideoIdForComments: Long? = null,
    val commentsPagingData: Flow<PagingData<io.paku.climblog.domain.model.comment.Comment>>? = null,
    val isPosting: Boolean = false
) : ViewModelState

sealed class SharedVideoViewModelEvent : ViewModelEvent {
    data class Init(val type: VideoListType, val initialVideoId: Long? = null) : SharedVideoViewModelEvent()
    data class OnLikeClick(val videoId: Long) : SharedVideoViewModelEvent()
    // Comment related events
    data class LoadComments(val videoId: Long) : SharedVideoViewModelEvent()
    data class PostComment(val content: String) : SharedVideoViewModelEvent()
}

internal class SharedVideoViewModel(
    private val toggleLikeUseCase: io.paku.climblog.domain.interactors.video.ToggleLikeUseCase,
    private val fetchUserUseCase: io.paku.climblog.domain.interactors.user.FetchUserUseCase,
    private val getCommentsUseCase: io.paku.climblog.domain.interactors.video.GetCommentsUseCase,
    private val postCommentUseCase: io.paku.climblog.domain.interactors.video.PostCommentUseCase,
    private val getVideoFeedUseCase: io.paku.climblog.domain.interactors.video.GetVideoFeedUseCase,
    private val getUserVideosUseCase: io.paku.climblog.domain.interactors.video.GetUserVideosUseCase,
    private val searchVideosUseCase: io.paku.climblog.domain.interactors.video.SearchVideosUseCase,
    private val getSingleVideoUseCase: io.paku.climblog.domain.interactors.video.GetSingleVideoUseCase
) : BaseViewModel<SharedVideoViewModelState, SharedVideoViewModelEvent, Nothing>() {

    init {
        launch {
            fetchUserUseCase().collectLatest { user ->
                val oldUserId = state.value.currentUserId
                updateState { copy(currentUserId = user.id) }
                
                if (oldUserId == null && state.value.videoListType is VideoListType.My) {
                    // Re-init with correct user ID if it was My and we just got the ID
                    val type = state.value.videoListType ?: VideoListType.My
                    init(type, state.value.initialVideoId)
                }
            }
        }
    }

    override fun createInitialState(): SharedVideoViewModelState = SharedVideoViewModelState()

    override fun createTriggerEvent(event: ViewModelEvent) {
        if (event is SharedVideoViewModelEvent) {
            when (event) {
                is SharedVideoViewModelEvent.Init -> init(event.type, event.initialVideoId)
                is SharedVideoViewModelEvent.OnLikeClick -> toggleLike(event.videoId)
                is SharedVideoViewModelEvent.LoadComments -> loadComments(event.videoId)
                is SharedVideoViewModelEvent.PostComment -> postComment(event.content)
            }
        }
    }

    private fun init(type: VideoListType, initialVideoId: Long?) {
        if (state.value.videoListType == type) return

        val flow = when (type) {
            is VideoListType.Home -> getVideoFeedUseCase()
            is VideoListType.My -> {
                val userId = state.value.currentUserId
                if (userId != null) {
                    getUserVideosUseCase(userId)
                } else {
                    getVideoFeedUseCase() // Fallback
                }
            }
            is VideoListType.User -> getUserVideosUseCase(type.userId)
            is VideoListType.Search -> searchVideosUseCase(type.query)
            is VideoListType.Single -> getSingleVideoUseCase(type.videoId)
        }.cachedIn(viewModelScope)

        updateState {
            copy(
                videoListType = type,
                videoPagingData = flow,
                initialVideoId = initialVideoId
            )
        }
    }

    private fun toggleLike(videoId: Long) = launch {
        val isCurrentlyLiked = state.value.likedVideoIds.contains(videoId)
        updateState {
            copy(
                likedVideoIds = if (isCurrentlyLiked) likedVideoIds - videoId else likedVideoIds + videoId
            )
        }
        
        toggleLikeUseCase(videoId).onFailure {
            updateState {
                copy(
                    likedVideoIds = if (isCurrentlyLiked) likedVideoIds + videoId else likedVideoIds - videoId
                )
            }
        }
    }

    private fun loadComments(videoId: Long) {
        if (state.value.activeVideoIdForComments == videoId) return

        val flow = getCommentsUseCase(videoId)
            .cachedIn(viewModelScope)
        
        updateState { copy(activeVideoIdForComments = videoId, commentsPagingData = flow) }
    }

    private fun postComment(content: String) = launch {
        val videoId = state.value.activeVideoIdForComments ?: return@launch
        updateState { copy(isPosting = true) }
        
        postCommentUseCase(videoId, content).onSuccess {
            updateState { copy(isPosting = false) }
            // Trigger refresh by reloading the flow
            val flow = getCommentsUseCase(videoId)
                .cachedIn(viewModelScope)
            updateState { copy(commentsPagingData = flow) }
        }.onFailure {
            updateState { copy(isPosting = false) }
        }
    }
    
    fun onEvent(event: SharedVideoViewModelEvent) {
        createTriggerEvent(event)
    }
}
