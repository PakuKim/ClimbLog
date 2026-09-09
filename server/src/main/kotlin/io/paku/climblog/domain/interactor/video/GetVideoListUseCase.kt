package io.paku.climblog.domain.interactor.video

import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.model.video.Video

class GetVideoListUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(
        type: String?,
        userId: Long?,
        currentUserId: Long?,
        cursor: Long?,
        limit: Int,
        sortBy: String,
        orderBy: String
    ): Result<List<Video>> = runCatching {
        when {
            userId != null -> {
                // 특정 사용자의 비디오 조회
                videoRepository.findAllPaged(
                    userId = userId,
                    cursor = cursor,
                    limit = limit,
                    sortBy = sortBy,
                    orderBy = orderBy
                )
            }
            type == "HOME" && currentUserId != null -> {
                // 홈 피드 조회 (팔로잉 + 랜덤)
                videoRepository.findHomeFeed(
                    currentUserId = currentUserId,
                    cursor = cursor,
                    limit = limit
                )
            }
            type == "RANDOM" -> {
                // 랜덤 비디오 조회
                videoRepository.findRandom(limit = limit)
            }
            else -> {
                // 전체 비디오 조회 (기본)
                videoRepository.findAllPaged(
                    cursor = cursor,
                    limit = limit,
                    sortBy = sortBy,
                    orderBy = orderBy
                )
            }
        }
    }
}