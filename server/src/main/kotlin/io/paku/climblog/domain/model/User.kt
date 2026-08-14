package io.paku.climblog.domain.model

data class User(
    val id: Long = 0L,
    val email: String,
    val passwordHash: String? = null,
    val name: String,
    val handle: String? = null,
    val age: Int? = null,
    val height: Int? = null,
    val armReach: Int? = null,
    val gender: String? = null,
    val profilePhotoUrl: String? = null
)
