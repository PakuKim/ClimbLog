package io.paku.climblog.presentation.ui.home

import androidx.lifecycle.viewModelScope
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import io.paku.climblog.business.data.source.remote.VideoPagingSource
import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.Comment
import io.paku.climblog.business.domain.model.Video
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.Event
import io.paku.climblog.presentation.base.State
import kotlinx.coroutines.flow.Flow

data class HomeFeedState(
    val pagingData: Flow<PagingData<Video>>? = null,
    val likedVideoIds: Set<Long> = emptySet(),
    val commentList: List<Comment> = emptyList(),
    val isCommentsLoading: Boolean = false
) : State

sealed class HomeFeedEvent : Event {
    object LoadFeed : HomeFeedEvent()
    data class ToggleLike(val videoId: Long) : HomeFeedEvent()
    data class LoadComments(val videoId: Long) : HomeFeedEvent()
    data class PostComment(val videoId: Long, val content: String) : HomeFeedEvent()
}

class HomeFeedViewModel(
    private val videoRepository: VideoRepository
) : BaseViewModel<HomeFeedState, HomeFeedEvent>() {

    override fun createInitialState(): HomeFeedState = HomeFeedState()

    override fun createTriggerEvent(event: Event) {
        if (event is HomeFeedEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: HomeFeedEvent) {
        when (event) {
            is HomeFeedEvent.LoadFeed -> loadFeed()
            is HomeFeedEvent.ToggleLike -> toggleLike(event.videoId)
            is HomeFeedEvent.LoadComments -> loadComments(event.videoId)
            is HomeFeedEvent.PostComment -> postComment(event.videoId, event.content)
        }
    }

    private fun loadFeed() {
        val flow = Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 10,
                enablePlaceholders = false,
                initialLoadSize = 10,
                maxSize = Int.MAX_VALUE,
                jumpThreshold = Int.MIN_VALUE
            ),
            initialKey = null,
            pagingSourceFactory = { VideoPagingSource(videoRepository) }
        ).flow.cachedIn(viewModelScope)
        
        updateState { copy(pagingData = flow) }
    }

    private fun toggleLike(videoId: Long) = launch {
        val isCurrentlyLiked = state.value.likedVideoIds.contains(videoId)
        updateState {
            copy(
                likedVideoIds = if (isCurrentlyLiked) likedVideoIds - videoId else likedVideoIds + videoId
            )
        }
        
        videoRepository.toggleLike(videoId).onFailure {
            updateState {
                copy(
                    likedVideoIds = if (isCurrentlyLiked) likedVideoIds + videoId else likedVideoIds - videoId
                )
            }
        }
    }

    private fun loadComments(videoId: Long) = launch {
        updateState { copy(isCommentsLoading = true, commentList = emptyList()) }
        videoRepository.getComments(videoId).onSuccess { comments ->
            updateState { copy(commentList = comments, isCommentsLoading = false) }
        }.onFailure {
            updateState { copy(isCommentsLoading = false) }
        }
    }

    private fun postComment(videoId: Long, content: String) = launch {
        videoRepository.postComment(videoId, content).onSuccess { newComment ->
            updateState { copy(commentList = listOf(newComment) + commentList) }
        }
    }
    
    init {
        loadFeed()
    }
}
