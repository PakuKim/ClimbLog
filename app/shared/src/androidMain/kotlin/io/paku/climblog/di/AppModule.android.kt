package io.paku.climblog.di

import io.paku.climblog.business.domain.model.SocialAuthType
import io.paku.climblog.business.domain.provider.GoogleLoginProviderImpl
import io.paku.climblog.business.domain.provider.KakaoLoginProviderImpl
import io.paku.climblog.business.domain.provider.NaverLoginProviderImpl
import io.paku.climblog.business.domain.provider.Provider
import io.paku.climblog.business.domain.provider.SocialLoginProviderImpl
import io.paku.climblog.business.domain.provider.social.SocialLoginProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformSocialModule: Module = module {
    single<SocialLoginProvider> {
        val providerMap = mapOf<SocialAuthType, Provider<SocialLoginProvider>>(
            SocialAuthType.GOOGLE to Provider { get<GoogleLoginProviderImpl>() },
            SocialAuthType.NAVER to Provider { get<NaverLoginProviderImpl>() },
            SocialAuthType.KAKAO to Provider { get<KakaoLoginProviderImpl>() }
        )

        SocialLoginProviderImpl(providers = providerMap)
    }

    factory { GoogleLoginProviderImpl(context = androidContext()) }
    factory { NaverLoginProviderImpl(context = androidContext()) }
    factory { KakaoLoginProviderImpl(context = androidContext()) }
}