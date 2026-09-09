package io.paku.climblog.presentation.ui.main.profile

import io.paku.climblog.presentation.base.ViewModelEvent

sealed class ProfileViewModelEvent: ViewModelEvent {
    object ToggleFollow : ProfileViewModelEvent()
}