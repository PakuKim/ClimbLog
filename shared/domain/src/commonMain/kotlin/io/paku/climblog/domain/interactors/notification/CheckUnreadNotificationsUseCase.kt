package io.paku.climblog.domain.interactors.notification

class CheckUnreadNotificationsUseCase(
    private val notificationRepository: io.paku.climblog.domain.NotificationRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return notificationRepository.checkUnread()
    }
}
