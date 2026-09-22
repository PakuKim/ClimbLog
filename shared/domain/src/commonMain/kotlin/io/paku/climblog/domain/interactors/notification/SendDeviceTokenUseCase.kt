package io.paku.climblog.domain.interactors.notification

class SendDeviceTokenUseCase(
    private val notificationRepository: io.paku.climblog.domain.NotificationRepository
) {
    suspend operator fun invoke(token: String): Result<Unit> {
        return notificationRepository.sendDeviceToken(token)
    }
}
