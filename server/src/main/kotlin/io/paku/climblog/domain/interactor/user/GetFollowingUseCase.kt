package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.UserFollowRepository
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.user.User

class GetFollowingUseCase(
    private val userRepository: UserRepository,
    private val userFollowRepository: UserFollowRepository
) {
    suspend operator fun invoke(userId: Long): Result<List<User>> = runCatching {
        val followingIds = userFollowRepository.getFollowingIds(userId)
        followingIds.mapNotNull { userRepository.findById(it) }
    }
}