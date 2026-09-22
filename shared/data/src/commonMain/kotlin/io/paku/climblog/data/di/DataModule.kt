package io.paku.climblog.data.di

import org.koin.dsl.module

val DataModule = module {
    single<io.paku.climblog.domain.SessionRepository> { _root_ide_package_.io.paku.climblog.data.SessionRepositoryImpl(get()) }
    single<io.paku.climblog.domain.AuthRepository> {
        _root_ide_package_.io.paku.climblog.data.AuthRepositoryImpl(
            get(),
            get(),
            get()
        )
    }
    single<io.paku.climblog.domain.UserRepository> {
        _root_ide_package_.io.paku.climblog.data.UserRepositoryImpl(
            get(),
            get(),
            get()
        )
    }
    single<io.paku.climblog.domain.VideoRepository> { _root_ide_package_.io.paku.climblog.data.VideoRepositoryImpl(get()) }
    single<io.paku.climblog.domain.NotificationRepository> {
        _root_ide_package_.io.paku.climblog.data.NotificationRepositoryImpl(
            get()
        )
    }
}
