package io.paku.climblog.di

import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.business.domain.provider.Provider
import io.paku.climblog.business.domain.provider.social.SocialLoginProvider
import io.paku.climblog.business.domain.provider.social.SocialLoginProviderImpl
import io.paku.climblog.core.IOSVideoCompressor
import io.paku.climblog.core.VideoCompressor
import io.paku.climblog.provider.social.GoogleLoginProviderImpl
import io.paku.climblog.provider.social.KakaoLoginProviderImpl
import io.paku.climblog.provider.social.NaverLoginProviderImpl
import io.paku.climblog.util.DataStoreUtil.createDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    // social
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
    factory { KakaoLoginProviderImpl() }

    // video
    single<VideoCompressor> { IOSVideoCompressor() }

    val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    single { createDataStore(coroutineScope) }
}
