package io.paku.climblog.domain.interactor.notification

import io.paku.climblog.domain.NotificationRepository

class CheckUnreadNotificationsUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(userId: Long): Result<Boolean> = runCatching {
        notificationRepository.hasUnread(userId)
    }
}
