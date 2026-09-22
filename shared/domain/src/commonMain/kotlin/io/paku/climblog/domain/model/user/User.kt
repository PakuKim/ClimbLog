package io.paku.climblog.domain.model.user

data class User(
    val id: Long,
    val name: String,
    val handle: String,
    val age: Int,
    val height: Int,
    val armReach: Int,
    val gender: String,
    val profilePhotoUrl: String? = null
)
