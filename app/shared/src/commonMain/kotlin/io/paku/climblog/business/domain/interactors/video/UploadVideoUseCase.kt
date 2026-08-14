package io.paku.climblog.business.domain.interactors.video

import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.Video

class UploadVideoUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String?,
        fileName: String,
        contentType: String,
        bytes: ByteArray,
        cruxStartTime: Double?,
        cruxEndTime: Double?,
        onProgress: (Float) -> Unit
    ): Result<Video> {
        return runCatching {
            // 1. Get Presigned URL
            val (presignedUrl, s3Key) = videoRepository.getPresignedUrl(fileName, contentType).getOrThrow()
            
            // 2. Upload to S3
            videoRepository.uploadToS3(presignedUrl, bytes, onProgress).getOrThrow()
            
            // 3. Register Metadata to Server
            videoRepository.registerVideo(
                title = title,
                description = description,
                s3Key = s3Key,
                cruxStartTime = cruxStartTime,
                cruxEndTime = cruxEndTime
            ).getOrThrow()
        }
    }
}
