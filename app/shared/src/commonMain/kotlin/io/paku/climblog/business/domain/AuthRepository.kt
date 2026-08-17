package io.paku.climblog.business.domain

interface AuthRepository {
    suspend fun register(
        email: String,
        password: String,
        name: String,
    ): Result<Unit>

    suspend fun login(
        email: String,
        password: String,
    ): Result<Unit>

    suspend fun socialLogin(
        provider: String,
        accessToken: String?,
        idToken: String?
    ): Result<Boolean> // returns isRegistered

    suspend fun logout(): Result<Unit>

    suspend fun checkEmail(
        email: String
    ): Result<Boolean>
}
