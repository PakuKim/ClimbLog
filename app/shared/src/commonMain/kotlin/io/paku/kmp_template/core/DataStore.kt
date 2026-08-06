package io.paku.kmp_template.core

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.CoroutineScope
import okio.Path
import org.koin.core.module.Module

internal const val DATA_STORE_FILE_NAME = "settings.preferences_pb"

expect val platformDataStoreModule: Module

object DataStoreFactory {
    fun create(
        scope: CoroutineScope,
        producePath: () -> Path
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = ReplaceFileCorruptionHandler {
                it.printStackTrace()
                emptyPreferences()
            },
            scope = scope,
            produceFile = { producePath() }
        )
    }
}