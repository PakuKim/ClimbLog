package io.paku.kmp_template.business.data.di

import io.paku.kmp_template.business.data.AuthRepositoryImpl
import io.paku.kmp_template.business.data.SessionRepositoryImpl
import io.paku.kmp_template.business.data.UserRepositoryImpl
import io.paku.kmp_template.business.domain.AuthRepository
import io.paku.kmp_template.business.domain.SessionRepository
import io.paku.kmp_template.business.domain.UserRepository
import org.koin.dsl.module

val DataModule = module {
    single<SessionRepository> { SessionRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
}