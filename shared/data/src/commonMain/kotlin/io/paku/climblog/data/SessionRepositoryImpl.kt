package io.paku.climblog.data

import kotlinx.coroutines.flow.Flow

internal class SessionRepositoryImpl(
    private val local: io.paku.climblog.data.source.local.SessionLocalDataSource
): io.paku.climblog.domain.SessionRepository {
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
