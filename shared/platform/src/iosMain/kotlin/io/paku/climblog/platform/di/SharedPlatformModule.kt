package io.paku.climblog.platform.di

import io.paku.climblog.core.AppDispatcher
import io.paku.climblog.core.NetworkConfig
import io.paku.climblog.domain.model.social.SocialLoginType
import io.paku.climblog.domain.provider.Provider
import io.paku.climblog.domain.provider.VideoCompressor
import io.paku.climblog.domain.provider.social.SocialLoginProvider
import io.paku.climblog.local.datastore.DataStoreFactory
import io.paku.climblog.local.room.RoomDatabaseFactory
import io.paku.climblog.platform.IOSVideoCompressor
import io.paku.climblog.platform.provider.SocialLoginProviderImpl
import io.paku.climblog.platform.provider.social.GoogleLoginProviderImpl
import io.paku.climblog.platform.provider.social.KakaoLoginProviderImpl
import io.paku.climblog.platform.provider.social.NaverLoginProviderImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val sharedPlatformModule = module {
    // Core
    single { NetworkConfig(baseUrl = "http://127.0.0.1:8080/api/v1/") }

    single<CoroutineDispatcher>(named(AppDispatcher.IO)) { Dispatchers.Default }
    single<CoroutineDispatcher>(named(AppDispatcher.MAIN)) { Dispatchers.Main }
    single<CoroutineDispatcher>(named(AppDispatcher.DEFAULT)) { Dispatchers.Default }

    // Local
    single { RoomDatabaseFactory() }
    single { DataStoreFactory() }

    // Platform
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

    single<VideoCompressor> { IOSVideoCompressor() }
}
