package io.paku.climblog.presentation.ui.main

import io.paku.climblog.business.domain.NotificationRepository
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.Event
import io.paku.climblog.presentation.base.State

data class MainState(
    val hasUnreadNotifications: Boolean = false
) : State

sealed class MainEvent : Event {
    object CheckUnreadNotifications : MainEvent()
}

class MainViewModel(
    private val notificationRepository: NotificationRepository
) : BaseViewModel<MainState, MainEvent>() {

    override fun createInitialState(): MainState = MainState()

    override fun createTriggerEvent(event: Event) {
        if (event is MainEvent.CheckUnreadNotifications) {
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
