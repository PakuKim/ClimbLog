package io.paku.kmp_template.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual val platformDataStoreModule: Module = module {
    val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    single { createDataStore(coroutineScope) }
}

@OptIn(ExperimentalForeignApi::class)
private fun createDataStore(scope: CoroutineScope): DataStore<Preferences> {
    return DataStoreFactory.create(
        scope = scope,
        producePath = {
            val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )
            (documentDirectory?.path + "/$DATA_STORE_FILE_NAME").toPath()
        }
    )
}