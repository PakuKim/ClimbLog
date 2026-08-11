package io.paku.kmp_template.domain.model

data class User(
    val id: Long = 0L,
    val email: String,
    val passwordHash: String,
    val name: String
)