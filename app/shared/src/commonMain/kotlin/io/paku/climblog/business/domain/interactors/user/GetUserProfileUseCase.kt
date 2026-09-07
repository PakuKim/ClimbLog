package io.paku.climblog.business.domain.interactors.user

import io.paku.climblog.business.domain.UserRepository
import io.paku.climblog.business.domain.model.UserProfile

class GetUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): UserProfile {
        return userRepository.getUserProfile(userId)
    }
}
