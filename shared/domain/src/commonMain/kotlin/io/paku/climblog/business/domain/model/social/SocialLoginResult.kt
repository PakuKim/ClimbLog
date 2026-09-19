package io.paku.climblog.business.domain.model.social

import kotlinx.serialization.Serializable

@Serializable
data class SocialLoginResult(
    val type: SocialLoginType,
    val token: String
)