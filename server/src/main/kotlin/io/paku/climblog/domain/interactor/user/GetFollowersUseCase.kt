package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.UserFollowRepository
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.user.User

class GetFollowersUseCase(
    private val userRepository: UserRepository,
    private val userFollowRepository: UserFollowRepository
) {
    suspend operator fun invoke(userId: Long): Result<List<User>> = runCatching {
        val followerIds = userFollowRepository.getFollowerIds(userId)
        followerIds.mapNotNull { userRepository.findById(it) }
    }
}