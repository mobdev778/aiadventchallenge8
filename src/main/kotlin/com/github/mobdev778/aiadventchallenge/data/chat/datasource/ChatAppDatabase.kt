package com.github.mobdev778.aiadventchallenge.data.chat.datasource

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.mobdev778.aiadventchallenge.data.chat.datasource.model.ChatEntity
import com.github.mobdev778.aiadventchallenge.data.chat.datasource.model.ChatMessageEntity
import com.github.mobdev778.aiadventchallenge.data.chat.datasource.model.StickyFactsEntity
import java.nio.file.Path

@Database(
    entities = [
        ChatEntity::class,
        ChatMessageEntity::class,
        StickyFactsEntity::class,
    ],
    version = 6,
    exportSchema = false,
)
abstract class ChatAppDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao

    abstract fun stickyFactsDao(): StickyFactsDao

    companion object {
        fun create(dbFile: Path): ChatAppDatabase {
            // Room on pure JVM requires an explicit SQLiteDriver.
            // We use the bundled driver (androidx.sqlite:sqlite-bundled).
            return Room.databaseBuilder<ChatAppDatabase>(
                name = dbFile.toAbsolutePath().toString(),
            )
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
    }
}