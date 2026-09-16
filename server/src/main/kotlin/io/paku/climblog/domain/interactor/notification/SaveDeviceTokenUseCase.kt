package io.paku.climblog.domain.interactor.notification

import io.paku.climblog.domain.NotificationRepository

class SaveDeviceTokenUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(userId: Long, token: String): Result<Unit> = runCatching {
        notificationRepository.saveDeviceToken(userId, token)
    }
}
