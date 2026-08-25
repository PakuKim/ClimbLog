package io.paku.climblog.presentation.ui.main.profile

import io.paku.climblog.presentation.base.ViewModelEvent

sealed class ProfileViewModelEvent: ViewModelEvent {
    data class LoadProfile(val userId: Long, val isMyProfile: Boolean) : ProfileViewModelEvent()
    object ToggleFollow : ProfileViewModelEvent()
}