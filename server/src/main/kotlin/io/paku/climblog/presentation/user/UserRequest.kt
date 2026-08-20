package io.paku.climblog.presentation.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val handle: String? = null,
    val name: String? = null,
    val age: Int? = null,
    val height: Int? = null,
    val armReach: Int? = null,
    val gender: String? = null,
    val profilePhotoUrl: String? = null
)
