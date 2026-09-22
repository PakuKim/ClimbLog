package io.paku.climblog.presentation.ui.main.profile

import androidx.paging.PagingData
import io.paku.climblog.presentation.base.ViewModelState
import kotlinx.coroutines.flow.Flow

data class ProfileViewModelState(
    val user: io.paku.climblog.domain.model.user.User? = null,
    val userProfile: io.paku.climblog.domain.model.user.UserProfile? = null,
    val videoPagingData: Flow<PagingData<io.paku.climblog.domain.model.video.Video>>? = null,
    val isMyProfile: Boolean = false,
    val isFollowingInProgress: Boolean = false
): ViewModelState
