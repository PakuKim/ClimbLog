package io.paku.climblog.fakes

import io.paku.climblog.business.domain.AuthRepository
import io.paku.climblog.business.domain.model.SocialLoginType

class FakeAuthRepository : AuthRepository {
    var socialLoginCalled = false
    var socialLoginError: Exception? = null

    override suspend fun socialLogin(socialLoginType: SocialLoginType, socialToken: String) {
        socialLoginCalled = true
        socialLoginError?.let { throw it }
    }

    override suspend fun socialRegister(
        socialToken: String,
        socialLoginType: SocialLoginType,
        handle: String,
        name: String,
        age: Int,
        height: Int,
        armReach: Int,
        gender: String,
        profilePhotoUrl: String?
    ) {
    }

    override suspend fun logout() {
    }
}
