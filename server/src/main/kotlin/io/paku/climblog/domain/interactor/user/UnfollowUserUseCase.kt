package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.UserFollowRepository

internal class UnfollowUserUseCase(
    private val userFollowRepository: UserFollowRepository
) {
    suspend operator fun invoke(followerId: Long, followingId: Long) {
        userFollowRepository.unfollow(followerId, followingId)
    }
}
