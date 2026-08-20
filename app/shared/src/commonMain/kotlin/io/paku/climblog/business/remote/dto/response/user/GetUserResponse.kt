package io.paku.climblog.business.remote.dto.response.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUserResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("handle")
    val handle: String,
    @SerialName("age")
    val age: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("armReach")
    val armReach: Int,
    @SerialName("gender")
    val gender: String,
    @SerialName("profilePhotoUrl")
    val profilePhotoUrl: String? = null
)
