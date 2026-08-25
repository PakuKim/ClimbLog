package io.paku.climblog.presentation.ui.main.profile

import io.paku.climblog.business.domain.model.UserProfile
import io.paku.climblog.business.domain.model.Video
import io.paku.climblog.presentation.base.ViewModelState

data class ProfileViewModelState(
    val userProfile: UserProfile? = null,
    val userVideos: List<Video> = emptyList(),
    val isMyProfile: Boolean = false,
    val isFollowingInProgress: Boolean = false
): ViewModelState
