package io.paku.climblog.business.remote.dto.response.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUserResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("email")
    val email: String,
    @SerialName("name")
    val name: String,
    @SerialName("handle")
    val handle: String? = null,
    @SerialName("age")
    val age: Int? = null,
    @SerialName("height")
    val height: Int? = null,
    @SerialName("armReach")
    val armReach: Int? = null,
    @SerialName("gender")
    val gender: String? = null,
    @SerialName("profilePhotoUrl")
    val profilePhotoUrl: String? = null
)
