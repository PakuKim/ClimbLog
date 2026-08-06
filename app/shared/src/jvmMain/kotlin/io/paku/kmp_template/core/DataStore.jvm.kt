package io.paku.kmp_template.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual val platformDataStoreModule: Module = module {
    single {
        DataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            producePath = { File(DATA_STORE_FILE_NAME).absolutePath.toPath() }
        )
    }
}

