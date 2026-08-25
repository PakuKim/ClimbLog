package io.paku.climblog.presentation.ui.main.notification

import io.paku.climblog.business.domain.NotificationRepository
import io.paku.climblog.business.domain.model.Notification
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState

data class NotificationViewModelState(
    val notifications: List<Notification> = emptyList()
) : ViewModelState

sealed class NotificationViewModelEvent : ViewModelEvent {
    object LoadNotifications : NotificationViewModelEvent()
}

class NotificationViewModel(
    private val notificationRepository: NotificationRepository
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
        notificationRepository.getNotifications().onSuccess { list ->
            updateState { copy(notifications = list) }
        }
    }

    init {
        loadNotifications()
    }
}
