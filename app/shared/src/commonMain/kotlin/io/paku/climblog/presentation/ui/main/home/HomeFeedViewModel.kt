package io.paku.climblog.presentation.ui.main.home

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.paku.climblog.business.data.source.remote.VideoPagingSource
import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.Comment
import io.paku.climblog.business.domain.model.Video
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState
import kotlinx.coroutines.flow.Flow

data class HomeFeedViewModelState(
    val pagingData: Flow<PagingData<Video>>? = null,
    val likedVideoIds: Set<Long> = emptySet(),
    val commentList: List<Comment> = emptyList(),
    val isCommentsLoading: Boolean = false
) : ViewModelState

sealed class HomeFeedViewModelEvent : ViewModelEvent {
    object LoadFeed : HomeFeedViewModelEvent()
    data class ToggleLike(val videoId: Long) : HomeFeedViewModelEvent()
    data class LoadComments(val videoId: Long) : HomeFeedViewModelEvent()
    data class PostComment(val videoId: Long, val content: String) : HomeFeedViewModelEvent()
}

class HomeFeedViewModel(
    private val videoRepository: VideoRepository
) : BaseViewModel<HomeFeedViewModelState, HomeFeedViewModelEvent, Nothing>() {

    override fun createInitialState(): HomeFeedViewModelState = HomeFeedViewModelState()

    override fun createTriggerEvent(event: ViewModelEvent) {
        if (event is HomeFeedViewModelEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: HomeFeedViewModelEvent) {
        when (event) {
            is HomeFeedViewModelEvent.LoadFeed -> loadFeed()
            is HomeFeedViewModelEvent.ToggleLike -> toggleLike(event.videoId)
            is HomeFeedViewModelEvent.LoadComments -> loadComments(event.videoId)
            is HomeFeedViewModelEvent.PostComment -> postComment(event.videoId, event.content)
        }
    }

    private fun loadFeed() {
        val flow = Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 10,
                enablePlaceholders = false,
                initialLoadSize = 10
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
