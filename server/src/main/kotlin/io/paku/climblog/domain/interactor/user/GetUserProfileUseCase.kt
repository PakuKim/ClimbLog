package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.FollowRepository
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.model.UserProfile

class GetUserProfileUseCase(
    private val userRepository: UserRepository,
    private val videoRepository: VideoRepository,
    private val followRepository: FollowRepository
) {
    suspend operator fun invoke(targetUserId: Long, currentUserId: Long?): Result<UserProfile> = runCatching {
        val user = userRepository.findById(targetUserId) ?: throw Exception("User not found")
        val followerCount = followRepository.getFollowerCount(targetUserId)
        val followingCount = followRepository.getFollowingCount(targetUserId)
        val videoCount = videoRepository.findAllByUserId(targetUserId).size.toLong()
        val isFollowing = currentUserId?.let { followRepository.isFollowing(it, targetUserId) } ?: false
        
        UserProfile(user, followerCount, followingCount, videoCount, isFollowing)
    }
}
