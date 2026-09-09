package io.paku.climblog.business.domain.interactors.user

import io.paku.climblog.business.domain.UserRepository
import io.paku.climblog.business.domain.model.user.User

class GetFollowingUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Result<List<User>> {
        return userRepository.getFollowing(userId)
    }
}
