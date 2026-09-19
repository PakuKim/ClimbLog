package io.paku.climblog.business.local

import io.paku.climblog.business.data.model.user.UserData
import io.paku.climblog.business.data.source.local.UserLocalDataSource
import io.paku.climblog.business.local.mapper.user.UserEntityMapper
import io.paku.climblog.business.local.room.dao.UserDao
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
