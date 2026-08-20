package io.paku.climblog.business.remote.di

import io.paku.climblog.business.data.source.remote.AuthRemoteDataSource
import io.paku.climblog.business.data.source.remote.NotificationRemoteDataSource
import io.paku.climblog.business.data.source.remote.UserRemoteDataSource
import io.paku.climblog.business.data.source.remote.VideoRemoteDataSource
import io.paku.climblog.business.remote.AuthRemoteDataSourceImpl
import io.paku.climblog.business.remote.NotificationRemoteDataSourceImpl
import io.paku.climblog.business.remote.UserRemoteDataSourceImpl
import io.paku.climblog.business.remote.VideoRemoteDataSourceImpl
import io.paku.climblog.business.remote.ktor.KtorHttpClientFactory
import org.koin.dsl.module

val RemoteModule = module {
    single { KtorHttpClientFactory.create(get()) }

    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(get()) }
    single<VideoRemoteDataSource> { VideoRemoteDataSourceImpl(get()) }
    single<NotificationRemoteDataSource> { NotificationRemoteDataSourceImpl(get()) }
}
