package io.paku.kmp_template.business.domain

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun fetch(): Flow<Long?>

    suspend fun clearAll()
}