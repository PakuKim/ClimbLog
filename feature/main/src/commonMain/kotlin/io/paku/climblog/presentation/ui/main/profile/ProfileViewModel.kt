package io.paku.climblog.presentation.ui.main.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import io.paku.climblog.navigation.AppNavigation
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import kotlinx.coroutines.flow.collectLatest

internal class ProfileViewModel(
    savedStateHandle: SavedStateHandle,
    private val fetchUserUseCase: io.paku.climblog.domain.interactors.user.FetchUserUseCase,
    private val getUserProfileUseCase: io.paku.climblog.domain.interactors.user.GetUserProfileUseCase,
    private val toggleFollowUseCase: io.paku.climblog.domain.interactors.user.ToggleFollowUseCase,
    private val getUserVideosUseCase: io.paku.climblog.domain.interactors.video.GetUserVideosUseCase,
) : BaseViewModel<ProfileViewModelState, ProfileViewModelEvent, Nothing>() {
    private val args: AppNavigation.UserProfile = savedStateHandle.toRoute()

    init {
        updateState {
            copy(isMyProfile = args.userId == null)
        }

        launch {
            fetchUserUseCase().collectLatest { user ->
                updateState { 
                    copy(
                        user = user,
                        isMyProfile = args.userId == null || args.userId == user.id
                    ) 
                }
                if (args.userId == null) {
                    loadProfile(user.id)
                    loadVideos(user.id)
                }
            }
        }

        args.userId?.let { userId ->
            loadProfile(userId)
            loadVideos(userId)
        }
    }

    private fun loadProfile(userId: Long) = launch {
        getUserProfileUseCase(userId).also { profile ->
            updateState { copy(userProfile = profile) }
        }
    }

    private fun loadVideos(userId: Long) {
        val flow = getUserVideosUseCase(userId)
            .cachedIn(viewModelScope)

        updateState { copy(videoPagingData = flow) }
    }

    override fun createInitialState(): ProfileViewModelState = ProfileViewModelState()

    override fun createTriggerEvent(event: ViewModelEvent) {
        if (event is ProfileViewModelEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: ProfileViewModelEvent) {
        when (event) {
            is ProfileViewModelEvent.ToggleFollow -> toggleFollow()
        }
    }

    private fun toggleFollow() = launch {
        val profile = state.value.userProfile ?: return@launch
        updateState { copy(isFollowingInProgress = true) }
        
        val isFollowing = profile.isFollowing
        toggleFollowUseCase(profile.user.id, isFollowing)

        updateState {
            copy(
                userProfile = profile.copy(
                    isFollowing = !isFollowing,
                    followerCount = if (!isFollowing) profile.followerCount + 1 else profile.followerCount - 1
                ),
                isFollowingInProgress = false
            )
        }
    }
}
