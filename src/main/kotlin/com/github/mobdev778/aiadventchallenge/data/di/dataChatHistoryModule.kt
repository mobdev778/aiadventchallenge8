package com.github.mobdev778.aiadventchallenge.data.di

import com.github.mobdev778.aiadventchallenge.data.chathistory.datasource.AppDatabase
import com.github.mobdev778.aiadventchallenge.data.chathistory.repository.ChatHistoryRepository
import org.koin.dsl.module
import java.nio.file.Files
import java.nio.file.Path

val dataChatHistoryModule = module {
    single {
        // Persist DB under the plugin working directory.
        // If you want a more IDE-native location, switch to PathManager.getSystemPath().
        val dir: Path = Path.of(".", ".aiadventchallenge")
        Files.createDirectories(dir)
        val dbFile = dir.resolve("chat_history.db")

        AppDatabase.create(dbFile)
    }

    single {
        val db: AppDatabase = get()
        db.chatDao()
    }

    single {
        ChatHistoryRepository(
            chatDao = get(),
        )
    }
}
