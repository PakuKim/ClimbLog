package io.paku.climblog.domain.interactors.user

import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.user.User

class GetFollowingUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Result<List<User>> {
        return userRepository.getFollowing(userId)
    }
}
