package io.paku.climblog.business.local.datastore

import okio.Path
import okio.Path.Companion.toPath
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DataStoreFactory {
    actual fun producePath(): Path {
        return File(DATA_STORE_FILE_NAME).absolutePath.toPath()
    }
}