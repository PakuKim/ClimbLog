package io.paku.climblog.presentation.ui.main

import io.paku.climblog.business.domain.NotificationRepository
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState

data class MainViewModelState(
    val hasUnreadNotifications: Boolean = false
) : ViewModelState

sealed class MainViewModelEvent : ViewModelEvent {
    object CheckUnreadNotifications : MainViewModelEvent()
}

internal class MainViewModel(
    private val notificationRepository: NotificationRepository
) : BaseViewModel<MainViewModelState, MainViewModelEvent, Nothing>() {

    override fun createInitialState(): MainViewModelState = MainViewModelState()

    override fun createTriggerEvent(event: ViewModelEvent) {
        if (event is MainViewModelEvent.CheckUnreadNotifications) {
            checkUnread()
        }
    }

    private fun checkUnread() = launch {
        notificationRepository.checkUnread().onSuccess { hasUnread ->
            updateState { copy(hasUnreadNotifications = hasUnread) }
        }
    }

    init {
        checkUnread()
    }
}
