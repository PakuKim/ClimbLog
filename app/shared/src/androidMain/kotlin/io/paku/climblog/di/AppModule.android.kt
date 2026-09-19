package io.paku.climblog.di

import io.paku.climblog.BuildKonfig
import io.paku.climblog.business.domain.model.social.SocialLoginType
import io.paku.climblog.business.domain.provider.Provider
import io.paku.climblog.business.domain.provider.encode.EncodeFileProvider
import io.paku.climblog.business.domain.provider.social.SocialLoginProvider
import io.paku.climblog.business.domain.provider.social.SocialLoginProviderImpl
import io.paku.climblog.business.local.datastore.DataStoreFactory
import io.paku.climblog.business.local.room.RoomDatabaseFactory
import io.paku.climblog.core.AppDispatcher
import io.paku.climblog.core.NetworkConfig
import io.paku.climblog.core.PlatformConfig
import io.paku.climblog.provider.encode.EncodeFileProviderImpl
import io.paku.climblog.provider.social.GoogleLoginProviderImpl
import io.paku.climblog.provider.social.KakaoLoginProviderImpl
import io.paku.climblog.provider.social.NaverLoginProviderImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule: Module = module {
    // config
    single { NetworkConfig(baseUrl = "http://10.0.2.2:8080/api/v1/") }
    single { 
        PlatformConfig(
            googleWebClientId = BuildKonfig.GOOGLE_WEB_CLIENT_ID,
            kakaoNativeAppKey = BuildKonfig.KAKAO_NATIVE_APP_KEY,
            naverClientId = BuildKonfig.NAVER_CLIENT_ID,
            naverClientSecret = BuildKonfig.NAVER_CLIENT_SECRET
        )
    }

    // dispatchers
    single<CoroutineDispatcher>(named(AppDispatcher.IO)) { Dispatchers.IO }
    single<CoroutineDispatcher>(named(AppDispatcher.MAIN)) { Dispatchers.Main }
    single<CoroutineDispatcher>(named(AppDispatcher.DEFAULT)) { Dispatchers.Default }

    // database
    single { RoomDatabaseFactory(androidContext()) }

    // datastore
    single { DataStoreFactory(androidContext()) }

    //social
    single<SocialLoginProvider> {
        val providerMap = mapOf<SocialLoginType, Provider<SocialLoginProvider>>(
            SocialLoginType.GOOGLE to Provider { get<GoogleLoginProviderImpl>() },
            SocialLoginType.NAVER to Provider { get<NaverLoginProviderImpl>() },
            SocialLoginType.KAKAO to Provider { get<KakaoLoginProviderImpl>() }
        )

        SocialLoginProviderImpl(providers = providerMap)
    }

    // These will be provided by :shared:platform's internal platformModule if we includes it,
    // but here we are defining them in the Glue module to pass BuildKonfig.
    factory { GoogleLoginProviderImpl(get<PlatformConfig>().googleWebClientId) }
    factory { KakaoLoginProviderImpl(androidContext()) }
    factory { NaverLoginProviderImpl() }

    //encode
    single<EncodeFileProvider> { EncodeFileProviderImpl(androidContext()) }
}
