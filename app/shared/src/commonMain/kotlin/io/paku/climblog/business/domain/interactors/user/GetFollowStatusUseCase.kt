package io.paku.climblog.business.domain.interactors.user

import io.paku.climblog.business.domain.UserRepository

class GetFollowStatusUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Result<Boolean> {
        return userRepository.getFollowStatus(userId)
    }
}
