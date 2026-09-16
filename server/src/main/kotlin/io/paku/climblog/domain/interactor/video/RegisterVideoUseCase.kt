package io.paku.climblog.domain.interactor.video

import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.model.video.Video
import io.paku.climblog.domain.model.video.VideoStatus
import io.paku.climblog.domain.provider.MediaConvertProvider

class RegisterVideoUseCase(
    private val videoRepository: VideoRepository,
    private val mediaConvertProvider: MediaConvertProvider
) {
    suspend operator fun invoke(
        video: Video,
        s3Bucket: String,
        s3Key: String,
        fileNameWithoutExt: String
    ): Result<Video> = runCatching {
        val savedVideo = videoRepository.save(video)
        
        // Trigger MediaConvert Job
        try {
            mediaConvertProvider.createHlsJob(
                videoId = savedVideo.id,
                inputPath = "s3://$s3Bucket/$s3Key",
                outputPath = "s3://$s3Bucket/processed/$fileNameWithoutExt/"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            videoRepository.updateStatus(savedVideo.id, VideoStatus.FAILED)
        }
        
        savedVideo
    }
}
