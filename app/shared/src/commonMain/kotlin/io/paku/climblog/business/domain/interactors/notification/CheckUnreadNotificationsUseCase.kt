package io.paku.climblog.business.domain.interactors.notification

import io.paku.climblog.business.domain.NotificationRepository

class CheckUnreadNotificationsUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return notificationRepository.checkUnread()
    }
}
