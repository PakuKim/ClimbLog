package io.paku.climblog.domain.interactors.user

class GetUserProfileUseCase(
    private val userRepository: io.paku.climblog.domain.UserRepository
) {
    suspend operator fun invoke(userId: Long): io.paku.climblog.domain.model.user.UserProfile {
        return userRepository.getUserProfile(userId)
    }
}
