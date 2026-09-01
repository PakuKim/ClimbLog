package io.paku.climblog.business.domain.provider.social

import io.paku.climblog.business.domain.model.SocialLoginResult
import io.paku.climblog.business.domain.model.SocialLoginType

interface SocialLoginProvider {
    suspend fun latestLoginResult(type: SocialLoginType): SocialLoginResult

    suspend fun login(type: SocialLoginType): SocialLoginResult

    suspend fun logout(type: SocialLoginType)
}