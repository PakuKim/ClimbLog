package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.FollowRepository

class ToggleFollowUseCase(
    private val followRepository: FollowRepository
) {
    suspend operator fun invoke(followerId: Long, followingId: Long): Result<Boolean> = runCatching {
        if (followerId == followingId) throw Exception("Cannot follow yourself")
        
        if (followRepository.isFollowing(followerId, followingId)) {
            followRepository.unfollow(followerId, followingId)
            false
        } else {
            followRepository.follow(followerId, followingId)
            true
        }
    }
}
