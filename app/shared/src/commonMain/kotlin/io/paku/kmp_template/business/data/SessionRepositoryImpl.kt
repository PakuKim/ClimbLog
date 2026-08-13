package io.paku.kmp_template.business.data

import io.paku.kmp_template.business.data.source.local.SessionLocalDataSource
import io.paku.kmp_template.business.domain.SessionRepository
import kotlinx.coroutines.flow.Flow

internal class SessionRepositoryImpl(
    private val local: SessionLocalDataSource
): SessionRepository {
    override fun fetch(): Flow<Long?> {
        return local.fetchUserId()
    }

    override suspend fun clearAll() {
        local.clear()
    }
}