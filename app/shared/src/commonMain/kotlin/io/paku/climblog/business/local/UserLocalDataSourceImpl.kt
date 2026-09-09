package io.paku.climblog.business.local

import io.paku.climblog.business.data.source.local.UserLocalDataSource
import io.paku.climblog.business.domain.model.user.User
import kotlinx.coroutines.flow.Flow

internal class UserLocalDataSourceImpl: UserLocalDataSource {
    override fun fetchUser(): Flow<User> {
        TODO("Not yet implemented")
    }
}