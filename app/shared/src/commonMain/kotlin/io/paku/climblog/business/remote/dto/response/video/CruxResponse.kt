package io.paku.climblog.business.remote.dto.response.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CruxResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("cruxStartTime")
    val cruxStartTime: Double,
    @SerialName("cruxEndTime")
    val cruxEndTime: Double
)
