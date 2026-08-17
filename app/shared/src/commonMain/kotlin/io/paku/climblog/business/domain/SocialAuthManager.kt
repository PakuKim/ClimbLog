package io.paku.climblog.business.domain

import io.paku.climblog.business.domain.model.SocialAuthResult
import io.paku.climblog.business.domain.model.SocialProvider

expect class SocialAuthManager() {
    suspend fun login(provider: SocialProvider): Result<SocialAuthResult>
}
