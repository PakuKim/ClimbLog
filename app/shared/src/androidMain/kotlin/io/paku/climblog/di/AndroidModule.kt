package io.paku.climblog.di

import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.business.domain.provider.Provider
import io.paku.climblog.business.domain.provider.encode.EncodeFileProvider
import io.paku.climblog.business.domain.provider.social.SocialLoginProvider
import io.paku.climblog.business.domain.provider.social.SocialLoginProviderImpl
import io.paku.climblog.provider.encode.EncodeFileProviderImpl
import io.paku.climblog.provider.social.GoogleLoginProviderImpl
import io.paku.climblog.provider.social.KakaoLoginProviderImpl
import io.paku.climblog.provider.social.NaverLoginProviderImpl
import io.paku.climblog.util.DataStoreUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    //social
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
    factory { KakaoLoginProviderImpl(androidContext()) }

    //encode
    single<EncodeFileProvider> { EncodeFileProviderImpl(androidContext()) }

    //dataStore
    val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    single { DataStoreUtil.createDataStore(androidContext(), coroutineScope) }
}