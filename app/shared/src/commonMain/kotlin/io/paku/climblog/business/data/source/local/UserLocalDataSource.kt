package io.paku.climblog.business.data.source.local

import io.paku.climblog.business.domain.model.user.User
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {
    fun fetchUser(userId: Long): Flow<User>

    suspend fun saveUser(user: User)

    suspend fun clearAll()
}