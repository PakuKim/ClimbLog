package io.paku.climblog.data.source.remote

interface VideoRemoteDataSource {
    suspend fun getPresignedPost(
        fileName: String,
        contentType: String
    ): io.paku.climblog.data.model.video.PresignedPostData

    suspend fun uploadVideoToS3Post(
        url: String,
        fields: Map<String, String>,
        videoBytes: ByteArray,
        onProgress: (Float) -> Unit
    )

    suspend fun registerVideo(
        title: String,
        description: String?,
        s3Key: String,
        cruxStartTime: Double?,
        cruxEndTime: Double?
    ): io.paku.climblog.data.model.video.VideoData

    // Feed & Interactions
    suspend fun getVideos(
        type: String? = null,
        userId: Long? = null,
        sortBy: String = "CREATED_AT",
        orderBy: String = "DESC",
        cursor: Long? = null,
        limit: Int = 10
    ): io.paku.climblog.data.model.video.VideoFeedData

    suspend fun toggleLike(videoId: Long): Boolean
    suspend fun getComments(videoId: Long, cursor: Long?, limit: Int): io.paku.climblog.data.model.comment.CommentFeedData
    suspend fun postComment(videoId: Long, content: String): io.paku.climblog.data.model.comment.CommentData
}
