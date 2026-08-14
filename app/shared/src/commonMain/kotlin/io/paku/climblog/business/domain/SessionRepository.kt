package io.paku.climblog.business.domain

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun fetch(): Flow<Long?>

    suspend fun saveSession(accessToken: String, refreshToken: String)

    suspend fun clearAll()
}
