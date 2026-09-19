package io.paku.climblog.di

import io.paku.climblog.core.AppDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val coreModule = module {
    single(named(AppDispatcher.IO)) { Dispatchers.Default } // No IO dispatcher on iOS by default in coroutines 1.x
    single(named(AppDispatcher.MAIN)) { Dispatchers.Main }
    single(named(AppDispatcher.DEFAULT)) { Dispatchers.Default }
}
