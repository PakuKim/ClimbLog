package io.paku.climblog.di

import io.paku.climblog.business.domain.model.social.SocialLoginType
import io.paku.climblog.business.domain.provider.Provider
import io.paku.climblog.business.domain.provider.encode.EncodeFileProvider
import io.paku.climblog.business.domain.provider.social.SocialLoginProvider
import io.paku.climblog.business.domain.provider.social.SocialLoginProviderImpl
import io.paku.climblog.business.local.datastore.DataStoreFactory
import io.paku.climblog.business.local.room.RoomDatabaseFactory
import io.paku.climblog.core.AndroidVideoCompressor
import io.paku.climblog.core.AppDispatcher
import io.paku.climblog.core.VideoCompressor
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

    factory { NaverLoginProviderImpl() }
    factory { GoogleLoginProviderImpl() }
    factory { KakaoLoginProviderImpl(androidContext()) }

    //encode
    single<EncodeFileProvider> { EncodeFileProviderImpl(androidContext()) }
    
    //video
    single<VideoCompressor> { AndroidVideoCompressor(androidContext()) }
}