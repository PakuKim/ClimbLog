package io.paku.climblog.data.di

import io.paku.climblog.data.AuthRepositoryImpl
import io.paku.climblog.data.NotificationRepositoryImpl
import io.paku.climblog.data.SessionRepositoryImpl
import io.paku.climblog.data.UserRepositoryImpl
import io.paku.climblog.data.VideoRepositoryImpl
import io.paku.climblog.domain.AuthRepository
import io.paku.climblog.domain.NotificationRepository
import io.paku.climblog.domain.SessionRepository
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.VideoRepository
import org.koin.dsl.module

val DataModule = module {
    single<SessionRepository> { SessionRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get(), get()) }
    single<VideoRepository> { VideoRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
}
