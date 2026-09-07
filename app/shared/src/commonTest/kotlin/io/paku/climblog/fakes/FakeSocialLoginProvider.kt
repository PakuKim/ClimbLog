package io.paku.climblog.fakes

import io.paku.climblog.business.domain.model.SocialLoginResult
import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.business.domain.provider.social.SocialLoginProvider

class FakeSocialLoginProvider : SocialLoginProvider {
    var loginResult: SocialLoginResult? = null
    var latestResult: SocialLoginResult? = null
    var logoutCalled = false

    override suspend fun latestLoginResult(type: SocialLoginType): SocialLoginResult {
        return latestResult ?: throw IllegalStateException("Fake latest result not set")
    }

    override suspend fun login(type: SocialLoginType): SocialLoginResult {
        return loginResult ?: throw IllegalStateException("Fake login result not set")
    }

    override suspend fun logout(type: SocialLoginType) {
        logoutCalled = true
    }
}
