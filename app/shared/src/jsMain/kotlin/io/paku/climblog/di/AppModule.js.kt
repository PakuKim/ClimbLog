package io.paku.climblog.di

import io.paku.climblog.core.AppDispatcher
import io.paku.climblog.core.NetworkConfig
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { NetworkConfig(baseUrl = "http://localhost:8080/api/v1/") }
    
    single(named(AppDispatcher.IO)) { Dispatchers.Default }
    single(named(AppDispatcher.MAIN)) { Dispatchers.Main }
    single(named(AppDispatcher.DEFAULT)) { Dispatchers.Default }
}
