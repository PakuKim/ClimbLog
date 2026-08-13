package io.paku.kmp_template.domain

interface RefreshTokenRepository {
    suspend fun save(
        userId: Long,
        refreshToken: String,
    )

    suspend fun validate(
        userId: Long,
        refreshToken: String
    ): Boolean

    suspend fun update(
        userId: Long,
        newRefreshToken: String
    )

    suspend fun delete(
        userId: Long
    )
}