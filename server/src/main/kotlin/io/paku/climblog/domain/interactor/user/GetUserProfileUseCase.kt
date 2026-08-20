package io.paku.climblog.domain.interactor.user

import io.ktor.http.HttpStatusCode
import io.paku.climblog.domain.UserFollowRepository
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.model.AppException
import io.paku.climblog.domain.model.user.UserProfile

internal class GetUserProfileUseCase(
    private val userRepository: UserRepository,
    private val videoRepository: VideoRepository,
    private val userFollowRepository: UserFollowRepository
) {
    suspend operator fun invoke(targetUserId: Long, currentUserId: Long?): UserProfile {
        val user = userRepository.findById(targetUserId) ?: throw AppException(
            HttpStatusCode.NotFound, "User not found"
        )
        val followerCount = userFollowRepository.getFollowerCount(targetUserId)
        val followingCount = userFollowRepository.getFollowingCount(targetUserId)
        val videoCount = videoRepository.findAllByUserId(targetUserId).size.toLong()
        val isFollowing = currentUserId?.let { userFollowRepository.isFollowing(it, targetUserId) } ?: false

        return UserProfile(user, followerCount, followingCount, videoCount, isFollowing)
    }
}
