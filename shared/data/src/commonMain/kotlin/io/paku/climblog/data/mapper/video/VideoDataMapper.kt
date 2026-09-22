package io.paku.climblog.data.mapper.video

import io.paku.climblog.core.BiMapper
import io.paku.climblog.data.model.video.CruxData
import io.paku.climblog.data.model.video.VideoData
import io.paku.climblog.domain.model.video.Crux
import io.paku.climblog.domain.model.video.Video

internal object VideoDataMapper : BiMapper<VideoData, Video> {
    override fun mapToRight(from: VideoData): Video {
        return Video(
            id = from.id,
            userId = from.userId,
            title = from.title,
            description = from.description,
            hlsUrl = from.hlsUrl,
            thumbnailUrl = from.thumbnailUrl,
            status = enumValueOf(from.status.name),
            createdAt = from.createdAt,
            cruxes = from.cruxes.map { it.toDomain() }
        )
    }

    override fun mapToLeft(from: Video): VideoData {
        return VideoData(
            id = from.id,
            userId = from.userId,
            title = from.title,
            description = from.description,
            hlsUrl = from.hlsUrl,
            thumbnailUrl = from.thumbnailUrl,
            status = enumValueOf(from.status.name),
            createdAt = from.createdAt,
            cruxes = from.cruxes.map { it.toData() }
        )
    }

    private fun CruxData.toDomain() =
        Crux(
            id = id,
            videoId = videoId,
            startTime = startTime,
            endTime = endTime
        )

    private fun Crux.toData() = CruxData(
        id = id,
        videoId = videoId,
        startTime = startTime,
        endTime = endTime
    )
}
