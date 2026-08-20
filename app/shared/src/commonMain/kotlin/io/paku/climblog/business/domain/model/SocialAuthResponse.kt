package io.paku.climblog.business.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SocialAuthResponse(
    val type: SocialAuthType,
    val token: String
)