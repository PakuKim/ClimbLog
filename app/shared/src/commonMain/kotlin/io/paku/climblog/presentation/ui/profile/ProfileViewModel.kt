package io.paku.climblog.presentation.ui.profile

import io.paku.climblog.business.domain.UserRepository
import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.UserProfile
import io.paku.climblog.business.domain.model.Video
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.Event
import io.paku.climblog.presentation.base.State

data class ProfileState(
    val userProfile: UserProfile? = null,
    val userVideos: List<Video> = emptyList(),
    val isMyProfile: Boolean = false,
    val isFollowingInProgress: Boolean = false
) : State

sealed class ProfileEvent : Event {
    data class LoadProfile(val userId: Long, val isMyProfile: Boolean) : ProfileEvent()
    object ToggleFollow : ProfileEvent()
}

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val videoRepository: VideoRepository
) : BaseViewModel<ProfileState, ProfileEvent>() {

    override fun createInitialState(): ProfileState = ProfileState()

    override fun createTriggerEvent(event: Event) {
        if (event is ProfileEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> loadProfile(event.userId, event.isMyProfile)
            is ProfileEvent.ToggleFollow -> toggleFollow()
        }
    }

    private fun loadProfile(userId: Long, isMyProfile: Boolean) = launch {
        updateState { copy(isMyProfile = isMyProfile) }
        
        userRepository.getUserProfile(userId).onSuccess { profile ->
            updateState { copy(userProfile = profile) }
        }
        
        videoRepository.getUserVideos(userId).onSuccess { videos ->
            updateState { copy(userVideos = videos) }
        }
    }

    private fun toggleFollow() = launch {
        val profile = state.value.userProfile ?: return@launch
        updateState { copy(isFollowingInProgress = true) }
        
        val isFollowing = profile.isFollowing
        val result = if (isFollowing) {
            userRepository.unfollow(profile.user.id)
        } else {
            userRepository.follow(profile.user.id)
        }

        result.onSuccess {
            updateState { 
                copy(
                    userProfile = profile.copy(
                        isFollowing = !isFollowing,
                        followerCount = if (!isFollowing) profile.followerCount + 1 else profile.followerCount - 1
                    ),
                    isFollowingInProgress = false
                ) 
            }
        }.onFailure {
            updateState { copy(isFollowingInProgress = false) }
        }
    }
}
