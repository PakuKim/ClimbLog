package io.paku.climblog.business.local.di

import io.paku.climblog.business.data.source.local.SessionLocalDataSource
import io.paku.climblog.business.local.SessionLocalDataSourceImpl
import org.koin.dsl.module

val LocalModule = module {
    single<SessionLocalDataSource> { SessionLocalDataSourceImpl(get()) }
}