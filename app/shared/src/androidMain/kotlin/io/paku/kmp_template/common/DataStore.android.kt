package io.paku.kmp_template.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.paku.kmp_template.getPlatform

actual fun createDataStore(): DataStore<Preferences> {
    getPlatform()
    TODO("Not yet implemented")
}