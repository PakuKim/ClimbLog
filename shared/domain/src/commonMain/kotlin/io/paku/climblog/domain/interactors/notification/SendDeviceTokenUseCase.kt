package io.paku.climblog.domain.interactors.notification

import io.paku.climblog.domain.NotificationRepository

class SendDeviceTokenUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(token: String): Result<Unit> {
        return notificationRepository.sendDeviceToken(token)
    }
}
