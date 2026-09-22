package io.paku.climblog.remote.di

import io.paku.climblog.core.NetworkConfig
import io.paku.climblog.data.source.remote.AuthRemoteDataSource
import io.paku.climblog.data.source.remote.NotificationRemoteDataSource
import io.paku.climblog.data.source.remote.UserRemoteDataSource
import io.paku.climblog.data.source.remote.VideoRemoteDataSource
import io.paku.climblog.remote.AuthRemoteDataSourceImpl
import io.paku.climblog.remote.NotificationRemoteDataSourceImpl
import io.paku.climblog.remote.UserRemoteDataSourceImpl
import io.paku.climblog.remote.VideoRemoteDataSourceImpl
import io.paku.climblog.remote.ktor.KtorHttpClientFactory
import org.koin.dsl.module

val RemoteModule = module {
    single { 
        val config: NetworkConfig = get()
        KtorHttpClientFactory.create(config.baseUrl, get())
    }

    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(get()) }
    single<VideoRemoteDataSource> { VideoRemoteDataSourceImpl(get()) }
    single<NotificationRemoteDataSource> { NotificationRemoteDataSourceImpl(get()) }
}
