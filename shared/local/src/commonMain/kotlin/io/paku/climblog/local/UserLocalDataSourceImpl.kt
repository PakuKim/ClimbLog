package io.paku.climblog.local

import io.paku.climblog.data.model.user.UserData
import io.paku.climblog.data.source.local.UserLocalDataSource
import io.paku.climblog.local.mapper.user.UserEntityMapper
import io.paku.climblog.local.room.dao.UserDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

internal class UserLocalDataSourceImpl(
    private val userDao: UserDao
): UserLocalDataSource {
    override fun fetchUser(userId: Long): Flow<UserData> {
        return userDao.fetchUser(userId)
            .filterNotNull()
            .map { UserEntityMapper.mapToRight(it) }
    }

    override suspend fun saveUser(user: UserData) {
        userDao.insert(UserEntityMapper.mapToLeft(user))
    }

    override suspend fun clearAll() {
        userDao.deleteAll()
    }
}
