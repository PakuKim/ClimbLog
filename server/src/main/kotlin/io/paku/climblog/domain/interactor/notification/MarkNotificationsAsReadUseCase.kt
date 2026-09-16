package io.paku.climblog.domain.interactor.notification

import io.paku.climblog.domain.NotificationRepository

class MarkNotificationsAsReadUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(userId: Long): Result<Unit> = runCatching {
        notificationRepository.markAsRead(userId)
    }
}
