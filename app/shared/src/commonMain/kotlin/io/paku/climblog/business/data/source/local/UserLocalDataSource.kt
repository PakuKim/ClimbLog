package io.paku.climblog.business.data.source.local

import io.paku.climblog.business.domain.model.user.User
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {
    fun fetchUser(): Flow<User>
}