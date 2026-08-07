package io.paku.kmp_template.business.domain

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun fetch(): Flow<String?>

    suspend fun clearAll()
}