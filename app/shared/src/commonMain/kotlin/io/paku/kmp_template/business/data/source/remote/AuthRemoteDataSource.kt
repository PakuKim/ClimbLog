package io.paku.kmp_template.business.data.source.remote

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

    suspend fun checkEmail(
        email: String
    ): Boolean
}