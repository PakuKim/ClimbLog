package io.paku.climblog.presentation.ui.main.home

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.paku.climblog.business.data.source.remote.VideoPagingSource
import io.paku.climblog.business.domain.interactors.auth.LogoutUseCase
import io.paku.climblog.business.domain.interactors.video.GetCommentsUseCase
import io.paku.climblog.business.domain.interactors.video.GetVideoFeedUseCase
import io.paku.climblog.business.domain.interactors.video.PostCommentUseCase
import io.paku.climblog.business.domain.interactors.video.ToggleLikeUseCase
import io.paku.climblog.business.domain.model.Comment
import io.paku.climblog.business.domain.model.Video
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState
import kotlinx.coroutines.flow.Flow

data class HomeViewModelState(
    val videoPagingData: Flow<PagingData<Video>>? = null,
    val likedVideoIds: Set<Long> = emptySet(),
    val commentList: List<Comment> = emptyList(),
    val isCommentsLoading: Boolean = false
) : ViewModelState

sealed class HomeViewModelEvent : ViewModelEvent {
    data class OnLikeClick(val videoId: Long) : HomeViewModelEvent()
    data class LoadComments(val videoId: Long) : HomeViewModelEvent()
    data class PostComment(val videoId: Long, val content: String) : HomeViewModelEvent()
    object Logout : HomeViewModelEvent()
}

internal class HomeFeedViewModel(
    private val getVideoFeedUseCase: GetVideoFeedUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val postCommentUseCase: PostCommentUseCase,
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel<HomeViewModelState, HomeViewModelEvent, Nothing>() {

    override fun createInitialState(): HomeViewModelState = HomeViewModelState()

    override fun createTriggerEvent(event: ViewModelEvent) {
        if (event is HomeViewModelEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: HomeViewModelEvent) {
        when (event) {
            is HomeViewModelEvent.OnLikeClick -> toggleLike(event.videoId)
            is HomeViewModelEvent.LoadComments -> loadComments(event.videoId)
            is HomeViewModelEvent.PostComment -> postComment(event.videoId, event.content)
            is HomeViewModelEvent.Logout -> logout()
        }
    }

    private fun logout() = launch {
        logoutUseCase()
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
            pagingSourceFactory = { VideoPagingSource(getVideoFeedUseCase) }
        ).flow.cachedIn(viewModelScope)
        
        updateState { copy(videoPagingData = flow) }
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

    private fun loadComments(videoId: Long) = launch {
        updateState { copy(isCommentsLoading = true, commentList = emptyList()) }
        getCommentsUseCase(videoId).onSuccess { comments ->
            updateState { copy(commentList = comments, isCommentsLoading = false) }
        }.onFailure {
            updateState { copy(isCommentsLoading = false) }
        }
    }

    private fun postComment(videoId: Long, content: String) = launch {
        postCommentUseCase(videoId, content).onSuccess { newComment ->
            updateState { copy(commentList = listOf(newComment) + commentList) }
        }
    }
    
    init {
        loadFeed()
    }
}
