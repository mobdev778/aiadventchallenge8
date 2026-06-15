package com.github.mobdev778.aiadventchallenge.data.profile.datasource

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.mobdev778.aiadventchallenge.data.profile.datasource.model.ProfileEntity
import java.nio.file.Path

@Database(
    entities = [ProfileEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class ProfileAppDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao

    companion object {
        fun create(dbFile: Path): ProfileAppDatabase {
            return Room.databaseBuilder<ProfileAppDatabase>(
                name = dbFile.toAbsolutePath().toString(),
            )
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
    }
}
