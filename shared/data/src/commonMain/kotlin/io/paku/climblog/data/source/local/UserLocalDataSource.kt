package io.paku.climblog.data.source.local

import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {
    fun fetchUser(userId: Long): Flow<io.paku.climblog.data.model.user.UserData>

    suspend fun saveUser(user: io.paku.climblog.data.model.user.UserData)

    suspend fun clearAll()
}