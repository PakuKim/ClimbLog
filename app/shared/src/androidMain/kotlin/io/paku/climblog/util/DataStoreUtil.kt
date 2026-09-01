package io.paku.climblog.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import io.paku.climblog.core.DATA_STORE_FILE_NAME
import io.paku.climblog.core.DataStoreFactory
import kotlinx.coroutines.CoroutineScope
import okio.Path.Companion.toOkioPath

object DataStoreUtil {
    fun createDataStore(context: Context, scope: CoroutineScope): DataStore<Preferences> {
        return DataStoreFactory.create(
            scope = scope,
            producePath = { context.preferencesDataStoreFile(DATA_STORE_FILE_NAME).toOkioPath() }
        )
    }
}