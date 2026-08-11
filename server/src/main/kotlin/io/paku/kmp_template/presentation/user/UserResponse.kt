package io.paku.kmp_template.presentation.user

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val email: String,
    val name: String
)