package io.paku.climblog.local.room

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

actual class RoomDatabaseFactory {
    actual fun createBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = File(System.getProperty("java.io.tmpdir"), "climblog.db")
        return Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath
        )
    }

    actual fun createDriver(): SQLiteDriver = BundledSQLiteDriver()
}
