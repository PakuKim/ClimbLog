package io.paku.climblog.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.paku.climblog.core.DATA_STORE_FILE_NAME
import io.paku.climblog.core.DataStoreFactory
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

object DataStoreUtil {
    @OptIn(ExperimentalForeignApi::class)
    fun createDataStore(scope: CoroutineScope): DataStore<Preferences> {
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
}