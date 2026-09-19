package io.paku.climblog.business.data.model.user

data class UserData(
    val id: Long,
    val name: String,
    val handle: String,
    val age: Int,
    val height: Int,
    val armReach: Int,
    val gender: String,
    val profilePhotoUrl: String? = null
)
