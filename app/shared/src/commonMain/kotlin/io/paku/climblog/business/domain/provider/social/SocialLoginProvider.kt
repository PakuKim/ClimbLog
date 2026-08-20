package io.paku.climblog.business.domain.provider.social

import io.paku.climblog.business.domain.model.SocialAuthResponse
import io.paku.climblog.business.domain.model.SocialAuthType

interface SocialLoginProvider {
    suspend fun latestLoginResult(type: SocialAuthType): SocialAuthResponse

    suspend fun login(type: SocialAuthType): SocialAuthResponse

    suspend fun logout(type: SocialAuthType)
}