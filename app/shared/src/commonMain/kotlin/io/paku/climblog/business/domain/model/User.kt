package io.paku.climblog.business.domain.model

data class User(
    val id: Long,
    val email: String,
    val name: String,
    val handle: String? = null,
    val age: Int? = null,
    val height: Int? = null,
    val armReach: Int? = null,
    val gender: String? = null,
    val profilePhotoUrl: String? = null
)
