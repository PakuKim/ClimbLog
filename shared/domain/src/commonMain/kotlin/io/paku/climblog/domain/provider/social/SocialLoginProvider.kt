package io.paku.climblog.domain.provider.social

interface SocialLoginProvider {
    suspend fun latestLoginResult(type: io.paku.climblog.domain.model.social.SocialLoginType): io.paku.climblog.domain.model.social.SocialLoginResult

    suspend fun login(type: io.paku.climblog.domain.model.social.SocialLoginType): io.paku.climblog.domain.model.social.SocialLoginResult

    suspend fun logout(type: io.paku.climblog.domain.model.social.SocialLoginType)
}