package io.paku.climblog.business.local

import io.paku.climblog.business.data.source.local.UserLocalDataSource
import io.paku.climblog.business.domain.model.user.User
import io.paku.climblog.business.local.mapper.UserLocalMapper
import io.paku.climblog.business.local.room.dao.UserDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

internal class UserLocalDataSourceImpl(
    private val userDao: UserDao
): UserLocalDataSource {
    override fun fetchUser(userId: Long): Flow<User> {
        return userDao.fetchUser(userId)
            .filterNotNull()
            .map(UserLocalMapper::mapToLeft)
    }

    override suspend fun saveUser(user: User) {
        userDao.insert(user.let(UserLocalMapper::mapToRight))
    }

    override suspend fun clearAll() {
        userDao.deleteAll()
    }
}