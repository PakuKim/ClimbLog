package io.paku.climblog.business.domain.provider

import io.paku.climblog.business.domain.model.SocialAuthResponse
import io.paku.climblog.business.domain.model.SocialAuthType

actual interface SocialLoginProvider {
    actual suspend fun latestLoginResult(type: SocialAuthType): SocialAuthResponse
    actual suspend fun login(type: SocialAuthType): SocialAuthResponse
    actual suspend fun logout(type: SocialAuthType)
}