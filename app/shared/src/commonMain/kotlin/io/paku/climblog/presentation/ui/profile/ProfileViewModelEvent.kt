package io.paku.climblog.presentation.ui.profile

import io.paku.climblog.presentation.base.Event

sealed class ProfileViewModelEvent: Event {
    data class LoadProfile(val userId: Long, val isMyProfile: Boolean) : ProfileViewModelEvent()
    object ToggleFollow : ProfileViewModelEvent()
}