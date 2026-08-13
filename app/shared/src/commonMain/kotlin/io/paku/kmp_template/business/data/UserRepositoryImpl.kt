package io.paku.kmp_template.business.data

import io.paku.kmp_template.business.data.source.local.SessionLocalDataSource
import io.paku.kmp_template.business.data.source.remote.UserRemoteDataSource
import io.paku.kmp_template.business.domain.UserRepository

internal class UserRepositoryImpl(
    private val remote: UserRemoteDataSource,
    private val session: SessionLocalDataSource
): UserRepository {
    override suspend fun getUser() {
        remote.getUser().also {
            session.saveUserId(it)
        }
    }
}