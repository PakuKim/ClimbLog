package io.paku.kmp_template.business.remote.di

import io.paku.kmp_template.business.data.source.remote.AuthRemoteDataSource
import io.paku.kmp_template.business.data.source.remote.UserRemoteDataSource
import io.paku.kmp_template.business.remote.AuthRemoteDataSourceImpl
import io.paku.kmp_template.business.remote.UserRemoteDataSourceImpl
import io.paku.kmp_template.business.remote.ktor.KtorHttpClientFactory
import org.koin.dsl.module

val RemoteModule = module {
    single { KtorHttpClientFactory.create(get()) }
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(get()) }
}