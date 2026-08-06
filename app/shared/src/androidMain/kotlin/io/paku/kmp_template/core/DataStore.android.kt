package io.paku.kmp_template.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toOkioPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDataStoreModule: Module = module {
    val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    single { createDataStore(androidContext(), coroutineScope) }
}

private fun createDataStore(context: Context, scope: CoroutineScope): DataStore<Preferences> {
    return DataStoreFactory.create(
        scope = scope,
        producePath = { context.preferencesDataStoreFile(DATA_STORE_FILE_NAME).toOkioPath() }
    )
}