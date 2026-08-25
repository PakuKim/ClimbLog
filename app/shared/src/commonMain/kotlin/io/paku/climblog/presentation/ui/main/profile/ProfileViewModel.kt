package io.paku.climblog.presentation.ui.main.profile

import io.paku.climblog.business.domain.UserRepository
import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent

internal class ProfileViewModel(
    private val userRepository: UserRepository,
    private val videoRepository: VideoRepository
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
        
        val profile = userRepository.getUserProfile(userId)
        updateState { copy(userProfile = profile) }
        
        videoRepository.getUserVideos(userId).onSuccess { videos ->
            updateState { copy(userVideos = videos) }
        }
    }

    private fun toggleFollow() = launch {
        val profile = state.value.userProfile ?: return@launch
        updateState { copy(isFollowingInProgress = true) }
        
        val isFollowing = profile.isFollowing
        userRepository.toggleFollow(profile.user.id, isFollowing)

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
