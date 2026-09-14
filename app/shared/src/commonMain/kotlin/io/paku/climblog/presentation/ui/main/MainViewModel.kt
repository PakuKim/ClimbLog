package io.paku.climblog.presentation.ui.main

import io.paku.climblog.business.domain.interactors.notification.CheckUnreadNotificationsUseCase
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState

data class MainViewModelState(
    val hasUnreadNotifications: Boolean = false,
    val isBottomBarVisible: Boolean = true
) : ViewModelState

sealed class MainViewModelEvent : ViewModelEvent {
    object CheckUnreadNotifications : MainViewModelEvent()
    data class SetBottomBarVisibility(val visible: Boolean) : MainViewModelEvent()
}

internal class MainViewModel(
    private val checkUnreadNotificationsUseCase: CheckUnreadNotificationsUseCase
) : BaseViewModel<MainViewModelState, MainViewModelEvent, Nothing>() {

    override fun createInitialState(): MainViewModelState = MainViewModelState()

    override fun createTriggerEvent(event: ViewModelEvent) {
        when (event) {
            is MainViewModelEvent.CheckUnreadNotifications -> checkUnread()
            is MainViewModelEvent.SetBottomBarVisibility -> updateState { copy(isBottomBarVisible = event.visible) }
        }
    }

    fun onEvent(event: MainViewModelEvent) {
        createTriggerEvent(event)
    }

    private fun checkUnread() = launch {
        checkUnreadNotificationsUseCase().onSuccess { hasUnread ->
            updateState { copy(hasUnreadNotifications = hasUnread) }
        }
    }

    init {
        checkUnread()
    }
}
