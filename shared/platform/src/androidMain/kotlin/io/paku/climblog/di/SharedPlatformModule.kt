package io.paku.climblog.di

import io.paku.climblog.BuildKonfig
import io.paku.climblog.business.domain.model.social.SocialLoginType
import io.paku.climblog.business.domain.provider.Provider
import io.paku.climblog.business.domain.provider.VideoCompressor
import io.paku.climblog.business.domain.provider.encode.EncodeFileProvider
import io.paku.climblog.business.domain.provider.social.SocialLoginProvider
import io.paku.climblog.business.local.datastore.DataStoreFactory
import io.paku.climblog.business.local.room.RoomDatabaseFactory
import io.paku.climblog.core.AndroidVideoCompressor
import io.paku.climblog.core.AppDispatcher
import io.paku.climblog.core.NetworkConfig
import io.paku.climblog.provider.SocialLoginProviderImpl
import io.paku.climblog.provider.encode.EncodeFileProviderImpl
import io.paku.climblog.provider.social.GoogleLoginProviderImpl
import io.paku.climblog.provider.social.KakaoLoginProviderImpl
import io.paku.climblog.provider.social.NaverLoginProviderImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val sharedPlatformModule = module {
    // Core
    single<CoroutineDispatcher>(named(AppDispatcher.IO)) { Dispatchers.IO }
    single<CoroutineDispatcher>(named(AppDispatcher.MAIN)) { Dispatchers.Main }
    single<CoroutineDispatcher>(named(AppDispatcher.DEFAULT)) { Dispatchers.Default }

    single { NetworkConfig(baseUrl = "http://10.0.2.2:8080/api/v1/") }

    // Local
    single { RoomDatabaseFactory(androidContext()) }
    single { DataStoreFactory(androidContext()) }

    // Platform
    single<EncodeFileProvider> { EncodeFileProviderImpl(androidContext()) }
    single<SocialLoginProvider> {
        val providerMap = mapOf<SocialLoginType, Provider<SocialLoginProvider>>(
            SocialLoginType.GOOGLE to Provider { get<GoogleLoginProviderImpl>() },
            SocialLoginType.NAVER to Provider { get<NaverLoginProviderImpl>() },
            SocialLoginType.KAKAO to Provider { get<KakaoLoginProviderImpl>() }
        )

        SocialLoginProviderImpl(providers = providerMap)
    }
    factory { GoogleLoginProviderImpl(BuildKonfig.GOOGLE_WEB_CLIENT_ID) }
    factory { KakaoLoginProviderImpl(androidContext()) }
    factory { NaverLoginProviderImpl() }


    single<VideoCompressor> { AndroidVideoCompressor(get()) }
}
