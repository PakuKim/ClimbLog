package io.paku.climblog.domain.interactors.notification

import io.paku.climblog.domain.NotificationRepository
import io.paku.climblog.domain.model.Notification

class GetNotificationsUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(): Result<List<Notification>> {
        return notificationRepository.getNotifications()
    }
}
