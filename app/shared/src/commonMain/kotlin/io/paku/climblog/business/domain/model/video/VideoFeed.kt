package io.paku.climblog.business.domain.model.video

data class VideoFeed(
    val items: List<Video>,
    val nextCursor: Long?
)
