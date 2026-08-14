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
        email: String,
        name: String,
        socialId: String,
        provider: String
    ): Triple<String, String, Boolean> // accessToken, refreshToken, isRegistered

    suspend fun checkEmail(
        email: String
    ): Boolean
}
