package io.paku.climblog.business.data

import io.paku.climblog.business.data.source.local.SessionLocalDataSource
import io.paku.climblog.business.data.source.remote.AuthRemoteDataSource
import io.paku.climblog.business.domain.AuthRepository
import io.paku.climblog.business.domain.SessionRepository

internal class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val sessionRepository: SessionRepository,
    private val sessionLocal: SessionLocalDataSource
): AuthRepository {
    override suspend fun register(email: String, password: String, name: String): Result<Unit> = runCatching {
        authRemoteDataSource.register(email, password, name)
    }

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val (accessToken, refreshToken) = authRemoteDataSource.login(email, password)
        sessionRepository.saveSession(accessToken, refreshToken)
    }

    override suspend fun socialLogin(
        provider: String,
        accessToken: String?,
        idToken: String?
    ): Result<Boolean> = runCatching {
        val (newAccessToken, refreshToken, isRegistered) = authRemoteDataSource.socialLogin(provider, accessToken, idToken)
        sessionRepository.saveSession(newAccessToken, refreshToken)
        isRegistered
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        authRemoteDataSource.logout()
        sessionRepository.clearAll()
    }

    override suspend fun checkEmail(email: String): Result<Boolean> = runCatching {
        authRemoteDataSource.checkEmail(email)
    }
}
