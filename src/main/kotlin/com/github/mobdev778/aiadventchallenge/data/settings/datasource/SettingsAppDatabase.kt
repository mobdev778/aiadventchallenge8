package com.github.mobdev778.aiadventchallenge.data.settings.datasource

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.mobdev778.aiadventchallenge.data.settings.datasource.model.SettingsEntity
import java.nio.file.Path

@Database(
    entities = [SettingsEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class SettingsAppDatabase : RoomDatabase() {

    abstract fun settingsDao(): SettingsDao

    companion object {
        fun create(dbFile: Path): SettingsAppDatabase {
            return Room.databaseBuilder<SettingsAppDatabase>(
                name = dbFile.toAbsolutePath().toString(),
            )
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
    }
}
