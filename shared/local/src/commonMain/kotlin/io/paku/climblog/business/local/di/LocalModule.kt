package io.paku.climblog.business.local.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.paku.climblog.business.data.source.local.SessionLocalDataSource
import io.paku.climblog.business.data.source.local.UserLocalDataSource
import io.paku.climblog.business.local.SessionLocalDataSourceImpl
import io.paku.climblog.business.local.UserLocalDataSourceImpl
import io.paku.climblog.business.local.datastore.createDataStore
import io.paku.climblog.business.local.room.AppDatabase
import io.paku.climblog.business.local.room.createDatabase
import io.paku.climblog.core.AppDispatcher
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
