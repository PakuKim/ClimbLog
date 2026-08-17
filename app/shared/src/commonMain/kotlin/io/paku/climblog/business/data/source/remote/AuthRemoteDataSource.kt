package io.paku.climblog.business.data.source.remote

interface AuthRemoteDataSource {
    suspend fun register(
        email: String,
        password: String,
        name: String,
    )

    suspend fun login (
        email: String,
        password: String,
    ): Pair<String, String>

    suspend fun socialLogin(
        provider: String,
        accessToken: String?,
        idToken: String?
    ): Triple<String, String, Boolean> // accessToken, refreshToken, isRegistered

    suspend fun logout()

    suspend fun checkEmail(
        email: String
    ): Boolean
}
