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
        email: String,
        name: String,
        socialId: String,
        provider: String
    ): Result<Boolean> // returns isRegistered

    suspend fun checkEmail(
        email: String
    ): Result<Boolean>
}
