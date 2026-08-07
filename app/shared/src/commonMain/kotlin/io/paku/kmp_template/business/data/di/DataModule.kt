package io.paku.kmp_template.business.data.di

import io.paku.kmp_template.business.data.SessionRepositoryImpl
import io.paku.kmp_template.business.domain.SessionRepository
import org.koin.dsl.module

val DataModule = module {
    single<SessionRepository> { SessionRepositoryImpl(get()) }
}