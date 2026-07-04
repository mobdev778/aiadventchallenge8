package com.github.mobdev778.aiadventchallenge.data.chat.datasource

import com.intellij.openapi.application.PathManager
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.nio.file.Files
import java.nio.file.Path

@Module
class ChatDatabaseModule {

    @Single
    fun provideChatAppDatabase(): ChatAppDatabase {
        val systemPath = PathManager.getSystemDir()
        val dir: Path = systemPath.resolve("aiadventchallenge")
        Files.createDirectories(dir)
        val dbFile = dir.resolve("chat_history.db")
        return ChatAppDatabase.create(dbFile)
    }

    @Single
    fun provideChatDao(chatAppDatabase: ChatAppDatabase): ChatDao {
        return chatAppDatabase.chatDao()
    }

    @Single
    fun provideStickyFactsDao(chatAppDatabase: ChatAppDatabase): StickyFactsDao {
        return chatAppDatabase.stickyFactsDao()
    }
}
