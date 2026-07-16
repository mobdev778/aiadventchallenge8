package com.github.mobdev778.aiadventchallenge.data.rag.datasource

import com.intellij.openapi.application.PathManager
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.nio.file.Files
import java.nio.file.Path

/**
 * Модуль Koin, отвечающий за предоставление зависимостей для доступа к базе данных RAG.
 * Создаёт файл базы данных "rag_documents.db" в поддиректории "aiadventchallenge"
 * системной директории IntelliJ и регистрирует экземпляры [RagAppDatabase] и [RagDocumentDao].
 */
@Module
class RagDatabaseModule {

    /**
     * Предоставляет единственный экземпляр [RagAppDatabase] для работы с векторным хранилищем документов.
     * При первом вызове создаёт необходимую директорию (если отсутствует) и инициализирует базу данных
     * по указанному пути.
     *
     * @return готовый к использованию экземпляр [RagAppDatabase].
     */
    @Single
    fun provideRagAppDatabase(): RagAppDatabase {
        val systemPath = PathManager.getSystemDir()
        val dir: Path = systemPath.resolve("aiadventchallenge")
        Files.createDirectories(dir)
        val dbFile = dir.resolve("rag_documents.db")
        return RagAppDatabase.create(dbFile)
    }

    /**
     * Предоставляет экземпляр [RagDocumentDao] для выполнения операций над документами в базе RAG.
     * Извлекает DAO из переданного экземпляра [RagAppDatabase].
     *
     * @param ragAppDatabase экземпляр базы данных, из которого будет получен DAO.
     * @return [RagDocumentDao], связанный с указанной базой данных.
     */
    @Single
    fun provideRagDocumentDao(ragAppDatabase: RagAppDatabase): RagDocumentDao {
        return ragAppDatabase.ragDocumentDao()
    }
}
