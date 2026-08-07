package io.paku.kmp_template.business.remote.di

import io.paku.kmp_template.business.remote.ktor.KtorHttpClientFactory
import org.koin.dsl.module

val RemoteModule = module {
    single { KtorHttpClientFactory.create(get()) }
}