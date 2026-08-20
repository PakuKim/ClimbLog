package io.paku.climblog.business.domain

import io.paku.climblog.business.domain.model.SocialAuthResult
import io.paku.climblog.business.domain.model.SocialProvider

actual class SocialAuthManager actual constructor() {
    actual suspend fun login(provider: SocialProvider): Result<SocialAuthResult> {
        return Result.failure(Exception("Social Auth is not implemented for JVM/Desktop"))
    }
}
