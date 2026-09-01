package io.paku.climblog.di

import io.paku.climblog.util.DataStoreUtil.createDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    single { createDataStore(coroutineScope) }
}