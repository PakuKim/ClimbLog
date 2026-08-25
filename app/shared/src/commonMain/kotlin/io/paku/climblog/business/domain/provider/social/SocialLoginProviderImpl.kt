package io.paku.climblog.business.domain.provider.social

import io.paku.climblog.business.domain.model.SocialLoginResult
import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.business.domain.provider.Provider
import io.paku.climblog.core.SocialLoginProvider

internal class SocialLoginProviderImpl(
    private val providers: Map<SocialLoginType, Provider<SocialLoginProvider>>
): SocialLoginProvider {
    override suspend fun latestLoginResult(type: SocialLoginType): SocialLoginResult {
        return providers[type]?.run {
            get().latestLoginResult(type)
        } ?: throw IllegalArgumentException("$type of parameter type is not allowed value")
    }

    override suspend fun login(type: SocialLoginType): SocialLoginResult {
        return providers[type]?.run {
            get().login(type)
        } ?: throw IllegalArgumentException("$type of parameter type is not allowed value")
    }

    override suspend fun logout(type: SocialLoginType) {
        providers.values.forEach { it.get().logout(type) }
    }
}