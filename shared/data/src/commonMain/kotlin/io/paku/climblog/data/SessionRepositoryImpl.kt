package io.paku.climblog.data

import io.paku.climblog.data.source.local.SessionLocalDataSource
import io.paku.climblog.domain.SessionRepository
import kotlinx.coroutines.flow.Flow

internal class SessionRepositoryImpl(
    private val local: SessionLocalDataSource
): SessionRepository {
    override fun fetch(): Flow<Long?> {
        return local.fetchUserId()
    }

    override suspend fun saveUserId(userId: Long) {
        local.saveUserId(userId)
    }

    override suspend fun saveSession(accessToken: String, refreshToken: String) {
        local.saveAccessToken(accessToken)
        local.saveRefreshToken(refreshToken)
    }

    override suspend fun clearAll() {
        local.clear()
    }
}
