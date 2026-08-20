package io.paku.climblog.business.data.source.remote

interface AuthRemoteDataSource {
    suspend fun socialLogin(
        provider: String,
        socialToken: String
    ): Pair<String, String>

    suspend fun socialRegister(
        socialToken: String,
        provider: String,
        handle: String,
        name: String,
        age: Int,
        height: Int,
        armReach: Int,
        gender: String,
        profilePhotoUrl: String?
    )

    suspend fun logout()
}
