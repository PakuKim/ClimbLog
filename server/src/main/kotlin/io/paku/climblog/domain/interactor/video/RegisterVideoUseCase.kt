package io.paku.climblog.domain.interactor.video

import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.model.video.Video
import io.paku.climblog.domain.model.video.VideoCrux
import io.paku.climblog.domain.model.video.VideoStatus

class RegisterVideoUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(
        userId: Long,
        title: String,
        description: String?,
        s3Key: String,
        cloudFrontDomain: String,
        cruxes: List<VideoCrux> = emptyList()
    ): Result<Video> = runCatching {
        val initialVideo = Video(
            userId = userId,
            title = title,
            description = description,
            hlsUrl = null,
            thumbnailUrl = null,
            status = VideoStatus.PROCESSING,
            videoCruxes = cruxes
        )
        videoRepository.registerVideoWithTranscodingJob(initialVideo, s3Key)
    }
}
