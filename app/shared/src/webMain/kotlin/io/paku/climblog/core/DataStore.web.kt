package io.paku.climblog.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDataStoreModule: Module = module {
    single {
        DataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
            producePath = { DATA_STORE_FILE_NAME.toPath() }
        )
    }
}