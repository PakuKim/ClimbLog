package io.paku.climblog.presentation.ui.profile

import io.paku.climblog.business.domain.model.UserProfile
import io.paku.climblog.business.domain.model.Video
import io.paku.climblog.presentation.base.State

data class ProfileViewModelState(
    val userProfile: UserProfile? = null,
    val userVideos: List<Video> = emptyList(),
    val isMyProfile: Boolean = false,
    val isFollowingInProgress: Boolean = false
): State
