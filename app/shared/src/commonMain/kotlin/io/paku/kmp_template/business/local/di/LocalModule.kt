package io.paku.kmp_template.business.local.di

import io.paku.kmp_template.business.data.source.local.SessionLocalDataSource
import io.paku.kmp_template.business.local.SessionLocalDataSourceImpl
import org.koin.dsl.module

val LocalModule = module {
    single<SessionLocalDataSource> { SessionLocalDataSourceImpl(get()) }
}