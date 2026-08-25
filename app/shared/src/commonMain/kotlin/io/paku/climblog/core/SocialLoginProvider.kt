package io.paku.climblog.core

import io.paku.climblog.business.domain.model.SocialLoginResult
import io.paku.climblog.business.domain.model.SocialLoginType
import org.koin.core.module.Module

interface SocialLoginProvider {
    suspend fun latestLoginResult(type: SocialLoginType): SocialLoginResult

    suspend fun login(type: SocialLoginType): SocialLoginResult

    suspend fun logout(type: SocialLoginType)
}

expect val platformSocialModule: Module