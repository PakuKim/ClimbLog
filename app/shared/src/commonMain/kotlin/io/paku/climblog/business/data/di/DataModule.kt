package io.paku.climblog.business.data.di

import io.paku.climblog.business.data.AuthRepositoryImpl
import io.paku.climblog.business.data.NotificationRepositoryImpl
import io.paku.climblog.business.data.SessionRepositoryImpl
import io.paku.climblog.business.data.UserRepositoryImpl
import io.paku.climblog.business.data.VideoRepositoryImpl
import io.paku.climblog.business.domain.AuthRepository
import io.paku.climblog.business.domain.NotificationRepository
import io.paku.climblog.business.domain.SessionRepository
import io.paku.climblog.business.domain.UserRepository
import io.paku.climblog.business.domain.VideoRepository
import org.koin.dsl.module

val DataModule = module {
    single<SessionRepository> { SessionRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<VideoRepository> { VideoRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
}
