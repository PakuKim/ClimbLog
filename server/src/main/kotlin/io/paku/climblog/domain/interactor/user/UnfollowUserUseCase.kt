package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.FollowRepository

class UnfollowUserUseCase(
    private val followRepository: FollowRepository
) {
    suspend operator fun invoke(followerId: Long, followingId: Long): Result<Unit> = runCatching {
        followRepository.unfollow(followerId, followingId)
    }
}
