package io.paku.climblog.domain.provider.social

import io.paku.climblog.domain.model.social.SocialLoginResult
import io.paku.climblog.domain.model.social.SocialLoginType

interface SocialLoginProvider {
    suspend fun latestLoginResult(type: SocialLoginType): SocialLoginResult

    suspend fun login(type: SocialLoginType): SocialLoginResult

    suspend fun logout(type: SocialLoginType)
}