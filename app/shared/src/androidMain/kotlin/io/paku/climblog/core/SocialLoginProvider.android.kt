package io.paku.climblog.core

import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.business.domain.provider.Provider
import io.paku.climblog.business.domain.provider.social.SocialLoginProviderImpl
import io.paku.climblog.provider.social.GoogleLoginProviderImpl
import io.paku.climblog.provider.social.KakaoLoginProviderImpl
import io.paku.climblog.provider.social.NaverLoginProviderImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformSocialModule: Module = module {
    single<SocialLoginProvider> {
        val providerMap = mapOf<SocialLoginType, Provider<SocialLoginProvider>>(
            SocialLoginType.GOOGLE to Provider { get<GoogleLoginProviderImpl>() },
            SocialLoginType.NAVER to Provider { get<NaverLoginProviderImpl>() },
            SocialLoginType.KAKAO to Provider { get<KakaoLoginProviderImpl>() }
        )

        SocialLoginProviderImpl(providers = providerMap)
    }

    factory { NaverLoginProviderImpl() }
    factory { GoogleLoginProviderImpl() }
    factory { KakaoLoginProviderImpl(context = androidContext()) }
}