package io.paku.climblog.domain.interactors.user

import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.user.UserProfile

class GetUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): UserProfile {
        return userRepository.getUserProfile(userId)
    }
}
