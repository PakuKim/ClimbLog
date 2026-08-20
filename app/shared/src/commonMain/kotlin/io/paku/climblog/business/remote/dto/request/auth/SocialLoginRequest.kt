package io.paku.climblog.business.remote.dto.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SocialLoginRequest(
    @SerialName("provider")
    val provider: String,
    @SerialName("socialToken")
    val socialToken: String
)
