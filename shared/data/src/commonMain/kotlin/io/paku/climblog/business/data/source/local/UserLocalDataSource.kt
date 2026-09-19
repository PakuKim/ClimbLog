package io.paku.climblog.business.data.source.local

import io.paku.climblog.business.data.model.user.UserData
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {
    fun fetchUser(userId: Long): Flow<UserData>

    suspend fun saveUser(user: UserData)

    suspend fun clearAll()
}