package io.paku.climblog.domain.model.video

data class VideoFeed(
    val items: List<Video>,
    val nextCursor: Long?
)
