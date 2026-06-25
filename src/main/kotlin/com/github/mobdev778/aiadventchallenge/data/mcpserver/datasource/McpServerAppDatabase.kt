package com.github.mobdev778.aiadventchallenge.data.mcpserver.datasource

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.mobdev778.aiadventchallenge.data.mcpserver.datasource.model.McpServerEntity
import com.github.mobdev778.aiadventchallenge.data.mymcpserver.datasource.MyMcpServerConfigDao
import com.github.mobdev778.aiadventchallenge.data.mymcpserver.datasource.model.MyMcpServerConfigEntity
import java.nio.file.Path

@Database(
    entities = [
        McpServerEntity::class,
        MyMcpServerConfigEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class McpServerAppDatabase : RoomDatabase() {

    abstract fun mcpServerDao(): McpServerDao

    abstract fun myMcpServerConfigDao(): MyMcpServerConfigDao

    companion object {
        fun create(dbFile: Path): McpServerAppDatabase {
            return Room.databaseBuilder<McpServerAppDatabase>(
                name = dbFile.toAbsolutePath().toString(),
            )
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
    }
}
