package io.paku.climblog.domain.model.token

import kotlinx.serialization.Serializable

@Serializable
data class NaverUserInfo(
    val response: NaverResponse? = null
)

@Serializable
data class NaverResponse(
    val id: String
)
