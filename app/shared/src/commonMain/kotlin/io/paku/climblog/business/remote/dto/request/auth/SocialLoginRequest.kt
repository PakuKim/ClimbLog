package io.paku.climblog.business.remote.dto.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SocialLoginRequest(
    @SerialName("email")
    val email: String,
    @SerialName("name")
    val name: String,
    @SerialName("socialId")
    val socialId: String,
    @SerialName("provider")
    val provider: String
)
