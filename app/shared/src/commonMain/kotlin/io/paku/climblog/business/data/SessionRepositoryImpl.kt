package io.paku.climblog.business.data

import io.paku.climblog.business.data.source.local.SessionLocalDataSource
import io.paku.climblog.business.domain.SessionRepository
import kotlinx.coroutines.flow.Flow

internal class SessionRepositoryImpl(
    private val local: SessionLocalDataSource
): SessionRepository {
    override fun fetch(): Flow<Long?> {
        return local.fetchUserId()
    }

    override suspend fun saveSession(accessToken: String, refreshToken: String) {
        local.saveAccessToken(accessToken)
        local.saveRefreshToken(refreshToken)
    }

    override suspend fun clearAll() {
        local.clear()
    }
}
