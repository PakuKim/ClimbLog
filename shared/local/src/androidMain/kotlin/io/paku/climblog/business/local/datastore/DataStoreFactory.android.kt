package io.paku.climblog.business.local.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStoreFile
import okio.Path
import okio.Path.Companion.toOkioPath

actual class DataStoreFactory(
    private val context: Context
) {
    actual fun producePath(): Path {
        return context.preferencesDataStoreFile(DATA_STORE_FILE_NAME).toOkioPath()
    }
}