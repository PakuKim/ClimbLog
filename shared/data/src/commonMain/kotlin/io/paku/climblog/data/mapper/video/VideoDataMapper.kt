package io.paku.climblog.data.mapper.video

import io.paku.climblog.core.BiMapper

internal object VideoDataMapper : BiMapper<io.paku.climblog.data.model.video.VideoData, io.paku.climblog.domain.model.video.Video> {
    override fun mapToRight(from: io.paku.climblog.data.model.video.VideoData): io.paku.climblog.domain.model.video.Video {
        return _root_ide_package_.io.paku.climblog.domain.model.video.Video(
            id = from.id,
            userId = from.userId,
            title = from.title,
            description = from.description,
            hlsUrl = from.hlsUrl,
            thumbnailUrl = from.thumbnailUrl,
            status = from.status,
            createdAt = from.createdAt,
            cruxes = from.cruxes.map { it.toDomain() }
        )
    }

    override fun mapToLeft(from: io.paku.climblog.domain.model.video.Video): io.paku.climblog.data.model.video.VideoData {
        return _root_ide_package_.io.paku.climblog.data.model.video.VideoData(
            id = from.id,
            userId = from.userId,
            title = from.title,
            description = from.description,
            hlsUrl = from.hlsUrl,
            thumbnailUrl = from.thumbnailUrl,
            status = from.status,
            createdAt = from.createdAt,
            cruxes = from.cruxes.map { it.toData() }
        )
    }

    private fun io.paku.climblog.data.model.video.CruxData.toDomain() =
        _root_ide_package_.io.paku.climblog.domain.model.video.Crux(
            id = id,
            videoId = videoId,
            startTime = startTime,
            endTime = endTime
        )

    private fun io.paku.climblog.domain.model.video.Crux.toData() = _root_ide_package_.io.paku.climblog.data.model.video.CruxData(
        id = id,
        videoId = videoId,
        startTime = startTime,
        endTime = endTime
    )
}
