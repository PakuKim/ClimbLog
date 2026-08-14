package io.paku.climblog.presentation.ui.notification

import io.paku.climblog.business.domain.NotificationRepository
import io.paku.climblog.business.domain.model.Notification
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.Event
import io.paku.climblog.presentation.base.State

data class NotificationState(
    val notifications: List<Notification> = emptyList()
) : State

sealed class NotificationEvent : Event {
    object LoadNotifications : NotificationEvent()
}

class NotificationViewModel(
    private val notificationRepository: NotificationRepository
) : BaseViewModel<NotificationState, NotificationEvent>() {

    override fun createInitialState(): NotificationState = NotificationState()

    override fun createTriggerEvent(event: Event) {
        if (event is NotificationEvent.LoadNotifications) {
            loadNotifications()
        }
    }

    fun onEvent(event: NotificationEvent) {
        when (event) {
            is NotificationEvent.LoadNotifications -> loadNotifications()
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
