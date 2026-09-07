package io.paku.climblog.presentation.ui.main.profile

import io.paku.climblog.business.domain.interactors.user.GetUserProfileUseCase
import io.paku.climblog.business.domain.interactors.user.ToggleFollowUseCase
import io.paku.climblog.business.domain.interactors.video.GetUserVideosUseCase
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent

internal class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val toggleFollowUseCase: ToggleFollowUseCase,
    private val getUserVideosUseCase: GetUserVideosUseCase
) : BaseViewModel<ProfileViewModelState, ProfileViewModelEvent, Nothing>() {

    override fun createInitialState(): ProfileViewModelState = ProfileViewModelState()

    override fun createTriggerEvent(event: ViewModelEvent) {
        if (event is ProfileViewModelEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: ProfileViewModelEvent) {
        when (event) {
            is ProfileViewModelEvent.LoadProfile -> loadProfile(event.userId, event.isMyProfile)
            is ProfileViewModelEvent.ToggleFollow -> toggleFollow()
        }
    }

    private fun loadProfile(userId: Long, isMyProfile: Boolean) = launch {
        updateState { copy(isMyProfile = isMyProfile) }
        
        val profile = getUserProfileUseCase(userId)
        updateState { copy(userProfile = profile) }
        
        getUserVideosUseCase(userId).onSuccess { videos ->
            updateState { copy(userVideos = videos) }
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
