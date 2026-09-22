package io.paku.climblog.remote.mapper.video

import io.paku.climblog.contract.video.CruxResponse
import io.paku.climblog.contract.video.VideoResponse
import io.paku.climblog.core.BiMapper
import io.paku.climblog.data.model.video.CruxData
import io.paku.climblog.data.model.video.VideoData

internal object VideoResponseMapper : BiMapper<VideoResponse, VideoData> {
    override fun mapToRight(from: VideoResponse): VideoData {
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

    override fun mapToLeft(from: VideoData): VideoResponse {
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
        CruxData(
            id = id,
            videoId = 0L,
            startTime = cruxStartTime,
            endTime = cruxEndTime
        )

    private fun CruxData.toResponse() = CruxResponse(
        id = id,
        cruxStartTime = startTime,
        cruxEndTime = endTime
    )
}
