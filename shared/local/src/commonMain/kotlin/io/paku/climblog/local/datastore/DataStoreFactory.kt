package io.paku.climblog.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.CoroutineScope
import okio.Path

internal const val DATA_STORE_FILE_NAME = "climblog.preferences_pb"

expect class DataStoreFactory {
    fun producePath(): Path
}

fun createDataStore(factory: DataStoreFactory, scope: CoroutineScope): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = ReplaceFileCorruptionHandler {
            it.printStackTrace()
            emptyPreferences()
        },
        scope = scope,
        produceFile = { factory.producePath() }
    )
}
