package io.paku.climblog.remote.mapper.video

import io.paku.climblog.contract.video.CruxResponse
import io.paku.climblog.contract.video.VideoResponse
import io.paku.climblog.core.BiMapper

internal object VideoResponseMapper : BiMapper<VideoResponse, io.paku.climblog.data.model.video.VideoData> {
    override fun mapToRight(from: VideoResponse): io.paku.climblog.data.model.video.VideoData {
        return _root_ide_package_.io.paku.climblog.data.model.video.VideoData(
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

    override fun mapToLeft(from: io.paku.climblog.data.model.video.VideoData): VideoResponse {
        return VideoResponse(
            id = from.id,
            userId = from.userId,
            title = from.title,
            description = from.description,
            hlsUrl = from.hlsUrl,
            thumbnailUrl = from.thumbnailUrl,
            status = enumValueOf(from.status.name),
            createdAt = from.createdAt,
            cruxes = from.cruxes.map { it.toResponse() }
        )
    }

    private fun CruxResponse.toData() =
        _root_ide_package_.io.paku.climblog.data.model.video.CruxData(
            id = id,
            videoId = 0L,
            startTime = cruxStartTime,
            endTime = cruxEndTime
        )

    private fun io.paku.climblog.data.model.video.CruxData.toResponse() = CruxResponse(
        id = id,
        cruxStartTime = startTime,
        cruxEndTime = endTime
    )
}
