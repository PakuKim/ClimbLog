package io.paku.climblog.domain.model.token

import kotlinx.serialization.Serializable

@Serializable
data class GoogleTokenInfo(
    val sub: String
)