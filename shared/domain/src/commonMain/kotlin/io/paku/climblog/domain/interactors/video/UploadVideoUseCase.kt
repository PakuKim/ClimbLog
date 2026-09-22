package io.paku.climblog.domain.interactors.video

class UploadVideoUseCase(
    private val videoRepository: io.paku.climblog.domain.VideoRepository,
    private val videoCompressor: io.paku.climblog.domain.provider.VideoCompressor
) {
    suspend operator fun invoke(
        title: String,
        description: String?,
        media: io.paku.climblog.domain.model.MediaSource,
        quality: io.paku.climblog.domain.model.video.VideoQuality,
        cruxStartTime: Double?,
        cruxEndTime: Double?,
        onProgress: (Float) -> Unit
    ): Result<io.paku.climblog.domain.model.video.Video> = runCatching {
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
