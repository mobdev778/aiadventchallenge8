package com.github.mobdev778.aiadventchallenge.data.chathistory.datasource

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.mobdev778.aiadventchallenge.data.chathistory.datasource.model.ChatMessageEntity
import java.nio.file.Path

@Database(
    entities = [ChatMessageEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    companion object {
        fun create(dbFile: Path): AppDatabase {
            // Room on pure JVM requires an explicit SQLiteDriver.
            // We use the bundled driver (androidx.sqlite:sqlite-bundled).
            return Room.databaseBuilder<AppDatabase>(
                name = dbFile.toAbsolutePath().toString(),
            )
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
    }
}