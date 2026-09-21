package io.paku.climblog.provider.social

import io.paku.climblog.business.domain.model.social.SocialLoginResult
import io.paku.climblog.business.domain.model.social.SocialLoginType
import io.paku.climblog.business.domain.provider.social.SocialLoginProvider

internal class NaverLoginProviderImpl : SocialLoginProvider {
    override suspend fun latestLoginResult(type: SocialLoginType): SocialLoginResult {
        // TODO: Implement iOS Naver Login
        throw UnsupportedOperationException("Naver Login not implemented on iOS yet")
    }

    override suspend fun login(type: SocialLoginType): SocialLoginResult {
        // TODO: Implement iOS Naver Login
        throw UnsupportedOperationException("Naver Login not implemented on iOS yet")
    }

    override suspend fun logout(type: SocialLoginType) {
        // TODO: Implement iOS Naver Logout
    }
}
