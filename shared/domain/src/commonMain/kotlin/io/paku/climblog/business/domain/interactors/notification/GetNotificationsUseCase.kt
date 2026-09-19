package io.paku.climblog.business.domain.interactors.notification

import io.paku.climblog.business.domain.NotificationRepository
import io.paku.climblog.business.domain.model.Notification

class GetNotificationsUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(): Result<List<Notification>> {
        return notificationRepository.getNotifications()
    }
}
