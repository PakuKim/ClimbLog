package io.paku.climblog.presentation.ui.main.notification

import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState

data class NotificationViewModelState(
    val notifications: List<io.paku.climblog.domain.model.Notification> = emptyList()
) : ViewModelState

sealed class NotificationViewModelEvent : ViewModelEvent {
    object LoadNotifications : NotificationViewModelEvent()
}

internal class NotificationViewModel(
    private val getNotificationsUseCase: io.paku.climblog.domain.interactors.notification.GetNotificationsUseCase
) : BaseViewModel<NotificationViewModelState, NotificationViewModelEvent, Nothing>() {

    override fun createInitialState(): NotificationViewModelState = NotificationViewModelState()

    override fun createTriggerEvent(event: ViewModelEvent) {
        if (event is NotificationViewModelEvent.LoadNotifications) {
            loadNotifications()
        }
    }

    fun onEvent(event: NotificationViewModelEvent) {
        when (event) {
            is NotificationViewModelEvent.LoadNotifications -> loadNotifications()
        }
    }

    private fun loadNotifications() = launchWithLoading {
        getNotificationsUseCase().onSuccess { list ->
            updateState { copy(notifications = list) }
        }
    }

    init {
        loadNotifications()
    }
}
