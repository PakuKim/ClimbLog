package io.paku.climblog.domain.interactors.notification

class GetNotificationsUseCase(
    private val notificationRepository: io.paku.climblog.domain.NotificationRepository
) {
    suspend operator fun invoke(): Result<List<io.paku.climblog.domain.model.Notification>> {
        return notificationRepository.getNotifications()
    }
}
