package io.paku.climblog.presentation.ui.main.profile

import io.paku.climblog.business.domain.model.user.User
import io.paku.climblog.business.domain.model.user.UserProfile
import io.paku.climblog.business.domain.model.video.Video
import io.paku.climblog.presentation.base.ViewModelState

data class ProfileViewModelState(
    val user: User? = null,
    val userProfile: UserProfile? = null,
    val userVideos: List<Video> = emptyList(),
    val isMyProfile: Boolean = false,
    val isFollowingInProgress: Boolean = false
): ViewModelState
