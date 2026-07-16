package com.github.mobdev778.aiadventchallenge.data.chat.datasource

import com.intellij.openapi.application.PathManager
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.nio.file.Files
import java.nio.file.Path

/**
 * Модуль Koin для зависимостей базы данных чата.
 *
 * Предоставляет в графе внедрения синглтоны:
 * - [ChatAppDatabase] — база данных Room с историей чатов и обработанными «липкими» фактами;
 * - [ChatDao] — DAO для работы с чатами и сообщениями;
 * - [StickyFactsDao] — DAO для работы с обработанными сообщениями «липких» фактов.
 *
 * База данных создаётся в подкаталоге `aiadventchallenge` системной директории IDE.
 */
@Module
class ChatDatabaseModule {

    /**
     * Предоставляет экземпляр [ChatAppDatabase].
     *
     * Создаёт (при необходимости) каталог `aiadventchallenge` внутри системной директории,
     * определяемой [PathManager.getSystemDir], и конструирует базу данных с файлом `chat_history.db`.
     *
     * @return готовый к использованию объект [ChatAppDatabase]
     */
    @Single
    fun provideChatAppDatabase(): ChatAppDatabase {
        val systemPath = PathManager.getSystemDir()
        val dir: Path = systemPath.resolve("aiadventchallenge")
        Files.createDirectories(dir)
        val dbFile = dir.resolve("chat_history.db")
        return ChatAppDatabase.create(dbFile)
    }

    /**
     * Предоставляет DAO для работы с чатами и сообщениями.
     *
     * Извлекает [ChatDao] из базы данных, созданной в [provideChatAppDatabase].
     *
     * @param chatAppDatabase экземпляр базы данных, полученный из контейнера Koin
     * @return объект [ChatDao] для выполнения операций с чатами и сообщениями
     */
    @Single
    fun provideChatDao(chatAppDatabase: ChatAppDatabase): ChatDao {
        return chatAppDatabase.chatDao()
    }

    /**
     * Предоставляет DAO для работы с обработанными сообщениями «липких» фактов.
     *
     * Извлекает [StickyFactsDao] из базы данных, созданной в [provideChatAppDatabase].
     *
     * @param chatAppDatabase экземпляр базы данных, полученный из контейнера Koin
     * @return объект [StickyFactsDao] для проверки и сохранения обработанных сообщений
     */
    @Single
    fun provideStickyFactsDao(chatAppDatabase: ChatAppDatabase): StickyFactsDao {
        return chatAppDatabase.stickyFactsDao()
    }
}
