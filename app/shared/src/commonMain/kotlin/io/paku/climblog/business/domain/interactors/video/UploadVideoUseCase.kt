package io.paku.climblog.business.domain.interactors.video

import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.Video
import io.paku.climblog.business.domain.model.VideoQuality
import io.paku.climblog.core.PlatformMedia
import io.paku.climblog.core.VideoCompressor

internal class UploadVideoUseCase(
    private val videoRepository: VideoRepository,
    private val videoCompressor: VideoCompressor
) {
    suspend operator fun invoke(
        title: String,
        description: String?,
        media: PlatformMedia,
        quality: VideoQuality,
        cruxStartTime: Double?,
        cruxEndTime: Double?,
        onProgress: (Float) -> Unit
    ): Result<Video> = runCatching {
        // 1. Video Compression
        val compressedMedia = videoCompressor.compress(media, quality)
        val videoBytes = compressedMedia.readBytes()

        // 2. Get Presigned POST Data
        val presignedPost = videoRepository.getPresignedPost(
            fileName = "video.mp4",
            contentType = "video/mp4"
        ).getOrThrow()

        // 3. Upload directly to S3 via Ktor Multipart
        videoRepository.uploadVideoToS3Post(
            url = presignedPost.url,
            fields = presignedPost.fields,
            videoBytes = videoBytes,
            onProgress = onProgress
        ).getOrThrow()

        // 4. Register Video Metadata to Server
        videoRepository.registerVideo(
            title = title,
            description = description,
            s3Key = presignedPost.objectKey,
            cruxStartTime = cruxStartTime,
            cruxEndTime = cruxEndTime
        ).getOrThrow()
    }
}
