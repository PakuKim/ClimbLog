package io.paku.kmp_template.business.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.paku.kmp_template.business.data.source.local.SessionLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class SessionLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>
): SessionLocalDataSource {
    companion object {
        private val USER_ID = longPreferencesKey("userId")
        private val ACCESS_TOKEN = stringPreferencesKey("accessToken")
        private val REFRESH_TOKEN = stringPreferencesKey("refreshToken")
    }

    override suspend fun saveUserId(userId: Long) {
        dataStore.edit { pref ->
            pref[USER_ID] = userId
        }
    }

    override fun fetchUserId(): Flow<Long?> {
        return dataStore.data.map { pref ->
            pref[USER_ID]
        }
    }
    override suspend fun updateAccessToken(accessToken: String) {
        dataStore.edit { pref ->
            pref[ACCESS_TOKEN] = accessToken
        }
    }

    override suspend fun updateRefreshToken(refreshToken: String) {
        dataStore.edit { pref ->
            pref[REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun getAccessToken(): String? {
        return dataStore.data.map { pref ->
            pref[ACCESS_TOKEN]
        }.firstOrNull()
    }

    override suspend fun getRefreshToken(): String? {
        return dataStore.data.map { pref ->
            pref[REFRESH_TOKEN]
        }.firstOrNull()
    }

    override suspend fun clear() {
        dataStore.edit { pref ->
            pref.remove(USER_ID)
            pref.remove(ACCESS_TOKEN)
            pref.remove(REFRESH_TOKEN)
        }
    }
}