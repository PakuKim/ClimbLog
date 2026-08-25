package io.paku.climblog.business.domain.provider

import io.paku.climblog.business.domain.model.SocialLoginResult
import io.paku.climblog.business.domain.model.SocialLoginType

actual interface SocialLoginProvider {
    actual suspend fun latestLoginResult(type: SocialLoginType): SocialLoginResult
    actual suspend fun login(type: SocialLoginType): SocialLoginResult
    actual suspend fun logout(type: SocialLoginType)
}