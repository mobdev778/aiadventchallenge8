package com.github.mobdev778.aiadventchallenge.data.mcpserver.datasource

import com.github.mobdev778.aiadventchallenge.data.mymcpserver.datasource.MyMcpServerConfigDao
import com.intellij.openapi.application.PathManager
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.nio.file.Files
import java.nio.file.Path

@Module
class McpServerDatabaseModule {

    @Single
    fun provideMcpServerAppDatabase(): McpServerAppDatabase {
        val systemPath = PathManager.getSystemDir()
        val dir: Path = systemPath.resolve("aiadventchallenge")
        Files.createDirectories(dir)
        val dbFile = dir.resolve("mcp_servers.db")
        return McpServerAppDatabase.create(dbFile)
    }

    @Single
    fun provideMcpServerDao(mcpServerAppDatabase: McpServerAppDatabase): McpServerDao {
        return mcpServerAppDatabase.mcpServerDao()
    }

    @Single
    fun provideMyMcpServerConfigDao(mcpServerAppDatabase: McpServerAppDatabase): MyMcpServerConfigDao {
        return mcpServerAppDatabase.myMcpServerConfigDao()
    }
}
