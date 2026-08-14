package io.paku.climblog.business.remote.dto.response.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckEmailResponse(
    @SerialName("isAvailable")
    val isAvailable: Boolean
)