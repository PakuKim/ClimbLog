package io.paku.climblog.local.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.paku.climblog.core.AppDispatcher
import io.paku.climblog.data.source.local.SessionLocalDataSource
import io.paku.climblog.data.source.local.UserLocalDataSource
import io.paku.climblog.local.SessionLocalDataSourceImpl
import io.paku.climblog.local.UserLocalDataSourceImpl
import io.paku.climblog.local.datastore.createDataStore
import io.paku.climblog.local.room.AppDatabase
import io.paku.climblog.local.room.createDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val LocalModule = module {
    single<AppDatabase> {
        createDatabase(
            factory = get(),
            queryContext = get<CoroutineDispatcher>(named(AppDispatcher.IO))
        )
    }
    single { get<AppDatabase>().userDao() }

    single<DataStore<Preferences>> {
        createDataStore(
            factory = get(),
            scope = CoroutineScope(get<CoroutineDispatcher>(named(AppDispatcher.IO)) + SupervisorJob())
        )
    }

    single<SessionLocalDataSource> { SessionLocalDataSourceImpl(get()) }
    single<UserLocalDataSource> { UserLocalDataSourceImpl(get()) }
}
