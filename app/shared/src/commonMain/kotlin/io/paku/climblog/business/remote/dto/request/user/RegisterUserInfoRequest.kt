package io.paku.climblog.business.remote.dto.request.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterUserInfoRequest(
    @SerialName("handle")
    val handle: String,
    @SerialName("name")
    val name: String,
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
