package io.paku.kmp_template.di

import io.paku.kmp_template.business.data.datasource.local.SessionLocalDataSource
import io.paku.kmp_template.business.local.SessionLocalDataSourceImpl
import io.paku.kmp_template.business.remote.ktor.KtorHttpClientFactory
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

fun appModule() = module {
    loadKoinModules(
        listOf(
            uiModule,
            dataModule,
            remoteModule,
            localModule
        )
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