package io.paku.climblog.domain.interactor.notification

import io.paku.climblog.domain.NotificationRepository
import io.paku.climblog.domain.model.Notification

class GetNotificationsUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(userId: Long): Result<List<Notification>> = runCatching {
        notificationRepository.findAllByUserId(userId)
    }
}
