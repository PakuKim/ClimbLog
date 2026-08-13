package io.paku.kmp_template.business.data

import io.paku.kmp_template.business.data.source.local.SessionLocalDataSource
import io.paku.kmp_template.business.data.source.remote.AuthRemoteDataSource
import io.paku.kmp_template.business.domain.AuthRepository

internal class AuthRepositoryImpl(
    private val remote: AuthRemoteDataSource,
    private val session: SessionLocalDataSource
): AuthRepository {
    override suspend fun checkEmail(email: String): Boolean {
        return remote.checkEmail(email)
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String
    ) {
        remote.register(
            email = email,
            password = password,
            name = name
        )
    }

    override suspend fun login(email: String, password: String) {
        remote.login(
            email = email,
            password = password
        ).also { (accessToken, refreshToken) ->
            session.updateAccessToken(accessToken)
            session.updateRefreshToken(refreshToken)
        }
    }
}