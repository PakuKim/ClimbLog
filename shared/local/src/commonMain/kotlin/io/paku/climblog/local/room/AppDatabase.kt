package io.paku.climblog.local.room

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.sqlite.SQLiteDriver
import io.paku.climblog.local.model.user.UserEntity
import io.paku.climblog.local.room.dao.UserDao
import kotlin.coroutines.CoroutineContext

@Database(
    entities = [UserEntity::class],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class RoomDatabaseFactory {
    fun createBuilder(): RoomDatabase.Builder<AppDatabase>
    fun createDriver(): SQLiteDriver
}

fun createDatabase(
    factory: RoomDatabaseFactory,
    queryContext: CoroutineContext
): AppDatabase {
    return factory.createBuilder()
        .fallbackToDestructiveMigration(true)
        .setDriver(factory.createDriver())
        .setQueryCoroutineContext(queryContext)
        .build()
}
