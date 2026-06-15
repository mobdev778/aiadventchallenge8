package com.github.mobdev778.aiadventchallenge.data.taskcontext.datasource

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.mobdev778.aiadventchallenge.data.taskcontext.datasource.model.TaskContextEntity
import java.nio.file.Path

@Database(
    entities = [TaskContextEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class TaskContextAppDatabase : RoomDatabase() {

    abstract fun taskContextDao(): TaskContextDao

    companion object {
        fun create(dbFile: Path): TaskContextAppDatabase {
            return Room.databaseBuilder<TaskContextAppDatabase>(
                name = dbFile.toAbsolutePath().toString(),
            )
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
    }
}
