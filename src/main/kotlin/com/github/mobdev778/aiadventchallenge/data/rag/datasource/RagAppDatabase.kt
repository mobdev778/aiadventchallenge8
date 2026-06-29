package com.github.mobdev778.aiadventchallenge.data.rag.datasource

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagDocumentChunkEntity
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagDocumentEntity
import java.nio.file.Path

@Database(
    entities = [RagDocumentEntity::class, RagDocumentChunkEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class RagAppDatabase : RoomDatabase() {

    abstract fun ragDocumentDao(): RagDocumentDao

    companion object {
        fun create(dbFile: Path): RagAppDatabase {
            return Room.databaseBuilder<RagAppDatabase>(
                name = dbFile.toAbsolutePath().toString(),
            )
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
    }
}
