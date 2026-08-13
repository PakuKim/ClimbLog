package io.paku.kmp_template.business.remote.dto.response.user

import kotlinx.serialization.Serializable

@Serializable
data class GetUserResponse(
    val id: Long,
    val email: String,
    val name: String
)