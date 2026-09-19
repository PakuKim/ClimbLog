package io.paku.climblog.business.local.datastore

import okio.Path
import okio.Path.Companion.toPath

actual class DataStoreFactory {
    actual fun producePath(): Path = "climblog.preferences_pb".toPath()
}
