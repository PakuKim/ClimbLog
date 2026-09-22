package io.paku.climblog.domain.interactors.user

import io.paku.climblog.domain.UserRepository

class GetFollowStatusUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Result<Boolean> {
        return userRepository.getFollowStatus(userId)
    }
}
