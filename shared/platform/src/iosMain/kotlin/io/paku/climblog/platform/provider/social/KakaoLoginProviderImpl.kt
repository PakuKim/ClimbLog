package io.paku.climblog.platform.provider.social

import io.paku.climblog.business.domain.model.social.SocialLoginResult
import io.paku.climblog.business.domain.model.social.SocialLoginType
import io.paku.climblog.business.domain.provider.social.SocialLoginProvider

internal class KakaoLoginProviderImpl : SocialLoginProvider {
    override suspend fun latestLoginResult(type: SocialLoginType): SocialLoginResult {
        // TODO: Implement iOS Kakao Login
        throw UnsupportedOperationException("Kakao Login not implemented on iOS yet")
    }

    override suspend fun login(type: SocialLoginType): SocialLoginResult {
        // TODO: Implement iOS Kakao Login
        throw UnsupportedOperationException("Kakao Login not implemented on iOS yet")
    }

    override suspend fun logout(type: SocialLoginType) {
        // TODO: Implement iOS Kakao Logout
    }
}
