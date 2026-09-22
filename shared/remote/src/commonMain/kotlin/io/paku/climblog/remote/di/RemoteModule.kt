package io.paku.climblog.remote.di

import io.paku.climblog.core.NetworkConfig
import org.koin.dsl.module

val RemoteModule = module {
    single { 
        val config: NetworkConfig = get()
        io.paku.climblog.remote.ktor.KtorHttpClientFactory.create(config.baseUrl, get())
    }

    single<io.paku.climblog.data.source.remote.AuthRemoteDataSource> {
        _root_ide_package_.io.paku.climblog.remote.AuthRemoteDataSourceImpl(
            get()
        )
    }
    single<io.paku.climblog.data.source.remote.UserRemoteDataSource> {
        _root_ide_package_.io.paku.climblog.remote.UserRemoteDataSourceImpl(
            get()
        )
    }
    single<io.paku.climblog.data.source.remote.VideoRemoteDataSource> {
        _root_ide_package_.io.paku.climblog.remote.VideoRemoteDataSourceImpl(
            get()
        )
    }
    single<io.paku.climblog.data.source.remote.NotificationRemoteDataSource> {
        _root_ide_package_.io.paku.climblog.remote.NotificationRemoteDataSourceImpl(
            get()
        )
    }
}
