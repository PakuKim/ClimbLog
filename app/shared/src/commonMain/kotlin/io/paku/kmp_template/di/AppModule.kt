package io.paku.kmp_template.di

import io.paku.kmp_template.business.data.datasource.local.SessionLocalDataSource
import io.paku.kmp_template.business.local.SessionLocalDataSourceImpl
import io.paku.kmp_template.business.remote.di.KtorHttpClientFactory
import io.paku.kmp_template.core.platformDataStoreModule
import org.koin.dsl.module

fun appModule() = module {
    includes(
        uiModule,
        dataModule,
        remoteModule,
        localModule,
        platformDataStoreModule
    )
}

val uiModule = module {

}

val dataModule = module {
    single<SessionLocalDataSource> { SessionLocalDataSourceImpl(get()) }
}

val remoteModule = module {
    single { KtorHttpClientFactory.create(get()) }
}

val localModule = module {
}