package io.paku.climblog.business.remote.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HandleCheckResponse(
    @SerialName("exists")
    val exists: Boolean
)
