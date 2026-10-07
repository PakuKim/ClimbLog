package io.paku.climblog.domain.interactors.video

import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.model.MediaSource
import io.paku.climblog.domain.model.video.Video
import io.paku.climblog.domain.model.video.VideoQuality
import io.paku.climblog.domain.provider.VideoCompressor

class UploadVideoUseCase(
    private val videoRepository: VideoRepository,
    private val videoCompressor: VideoCompressor
) {
    suspend operator fun invoke(
        title: String,
        description: String?,
        media: MediaSource,
        quality: VideoQuality,
        cruxStartTime: Double?,
        cruxEndTime: Double?,
        onProgress: (Float) -> Unit
    ): Result<Video> = runCatching {
        // 1. Video Compression
        val compressedMedia = videoCompressor.compress(media, quality)
        val videoBytes = compressedMedia.readBytes()

        // 2. Get Presigned PUT URL
        val presignedPut = videoRepository.getPresignedPut(
            fileName = "video.mp4",
            contentType = "video/mp4"
        ).getOrThrow()

        // 3. Upload directly to Cloudflare R2 via HTTP PUT with matching Content-Type
        videoRepository.uploadVideoToR2Put(
            uploadUrl = presignedPut.uploadUrl,
            contentType = "video/mp4",
            videoBytes = videoBytes,
            onProgress = onProgress
        ).getOrThrow()

        // 4. Register Video Metadata to Server
        videoRepository.registerVideo(
            title = title,
            description = description,
            s3Key = presignedPut.objectKey,
            cruxStartTime = cruxStartTime,
            cruxEndTime = cruxEndTime
        ).getOrThrow()
    }
}
