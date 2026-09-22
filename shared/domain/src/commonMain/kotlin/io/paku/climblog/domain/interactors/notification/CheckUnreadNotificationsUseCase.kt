package io.paku.climblog.domain.interactors.notification

import io.paku.climblog.domain.NotificationRepository

class CheckUnreadNotificationsUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return notificationRepository.checkUnread()
    }
}
