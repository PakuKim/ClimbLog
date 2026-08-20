package io.paku.climblog.business.domain.provider.social

import io.paku.climblog.business.domain.model.SocialAuthType

internal class SocialLoginProviderImpl(
    private val providers: Map<SocialAuthType, Provider<SocialLoginProvider>>
): SocialLoginProvider {
    override suspend fun latestLoginResult(type: SocialAuthType): SocialAuthResponse {
        return providers[type]?.run {
            get().latestLoginResult(type)
        } ?: throw IllegalArgumentException("$type of parameter type is not allowed value")
    }

    override suspend fun login(type: SocialAuthType): SocialAuthResponse {
        return providers[type]?.run {
            get().login(type)
        } ?: throw IllegalArgumentException("$type of parameter type is not allowed value")
    }

    override suspend fun logout(type: SocialAuthType) {
        providers.values.forEach { it.get().logout(type) }
    }
}