package io.paku.climblog.domain

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun fetch(): Flow<Long?>

    suspend fun saveUserId(userId: Long)

    suspend fun saveSession(accessToken: String, refreshToken: String)

    suspend fun clearAll()
}
