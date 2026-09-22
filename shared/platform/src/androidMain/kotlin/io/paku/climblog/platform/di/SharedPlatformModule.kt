package io.paku.climblog.platform.di

import io.paku.climblog.BuildKonfig
import io.paku.climblog.core.AppDispatcher
import io.paku.climblog.core.NetworkConfig
import io.paku.climblog.local.datastore.DataStoreFactory
import io.paku.climblog.local.room.RoomDatabaseFactory
import io.paku.climblog.platform.AndroidVideoCompressor
import io.paku.climblog.platform.provider.SocialLoginProviderImpl
import io.paku.climblog.platform.provider.encode.EncodeFileProviderImpl
import io.paku.climblog.platform.provider.social.GoogleLoginProviderImpl
import io.paku.climblog.platform.provider.social.KakaoLoginProviderImpl
import io.paku.climblog.platform.provider.social.NaverLoginProviderImpl
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
    single<io.paku.climblog.domain.provider.encode.EncodeFileProvider> { EncodeFileProviderImpl(androidContext()) }
    single<io.paku.climblog.domain.provider.social.SocialLoginProvider> {
        val providerMap = mapOf<io.paku.climblog.domain.model.social.SocialLoginType, io.paku.climblog.domain.provider.Provider<io.paku.climblog.domain.provider.social.SocialLoginProvider>>(
            io.paku.climblog.domain.model.social.SocialLoginType.GOOGLE to _root_ide_package_.io.paku.climblog.domain.provider.Provider { get<GoogleLoginProviderImpl>() },
            io.paku.climblog.domain.model.social.SocialLoginType.NAVER to _root_ide_package_.io.paku.climblog.domain.provider.Provider { get<NaverLoginProviderImpl>() },
            io.paku.climblog.domain.model.social.SocialLoginType.KAKAO to _root_ide_package_.io.paku.climblog.domain.provider.Provider { get<KakaoLoginProviderImpl>() }
        )

        SocialLoginProviderImpl(providers = providerMap)
    }
    factory { GoogleLoginProviderImpl(BuildKonfig.GOOGLE_WEB_CLIENT_ID) }
    factory { KakaoLoginProviderImpl(androidContext()) }
    factory { NaverLoginProviderImpl() }


    single<io.paku.climblog.domain.provider.VideoCompressor> { AndroidVideoCompressor(get()) }
}
