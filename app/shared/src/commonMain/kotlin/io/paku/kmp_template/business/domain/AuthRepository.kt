package io.paku.kmp_template.business.domain

interface AuthRepository {
    suspend fun checkEmail(
        email: String
    ): Boolean

    suspend fun register(
        email: String,
        password: String,
        name: String
    )

    suspend fun login(
        email: String,
        password: String
    )
}