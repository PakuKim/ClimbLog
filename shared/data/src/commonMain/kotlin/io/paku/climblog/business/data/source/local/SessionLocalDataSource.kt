package io.paku.climblog.business.data.source.local

import kotlinx.coroutines.flow.Flow

interface SessionLocalDataSource {
    suspend fun saveUserId(userId: Long)

    fun fetchUserId(): Flow<Long?>

    suspend fun saveAccessToken(accessToken: String)

    suspend fun saveRefreshToken(refreshToken: String)

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun clear()
}
