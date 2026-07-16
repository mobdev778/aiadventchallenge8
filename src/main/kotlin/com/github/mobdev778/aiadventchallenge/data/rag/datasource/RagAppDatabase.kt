package com.github.mobdev778.aiadventchallenge.data.rag.datasource

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagConfigEntity
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagDocumentChunkEntity
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagDocumentEntity
import java.nio.file.Path

/**
 * Комнатная база данных для системы Retrieval-Augmented Generation (RAG).
 *
 * Управляет сущностями документов ([RagDocumentEntity]), их фрагментов ([RagDocumentChunkEntity])
 * и конфигурации RAG ([RagConfigEntity]). Предоставляет DAO ([RagDocumentDao]) для выполнения
 * всех операций наблюдения, вставки, удаления и конфигурирования с этими данными.
 *
 * Версия базы данных: 5.
 * При несовместимости схемы применяется деструктивная миграция (пересоздание всех таблиц).
 */
@Database(
    entities = [RagDocumentEntity::class, RagDocumentChunkEntity::class, RagConfigEntity::class],
    version = 5,
    exportSchema = false,
)
abstract class RagAppDatabase : RoomDatabase() {

    /**
     * Возвращает объект доступа к данным (DAO) для сущностей RAG-системы.
     *
     * @return [RagDocumentDao], предоставляющий асинхронные методы работы с документами,
     *         их фрагментами и конфигурацией.
     */
    abstract fun ragDocumentDao(): RagDocumentDao

    companion object {
        /**
         * Создаёт и возвращает экземпляр [RagAppDatabase], связанный с указанным файлом базы данных.
         *
         * Используется драйвер [BundledSQLiteDriver]. При изменении схемы, не поддерживающем миграцию,
         * все таблицы удаляются и создаются заново (fallbackToDestructiveMigration с dropAllTables = true).
         *
         * @param dbFile Путь к файлу базы данных SQLite.
         * @return Готовый к использованию экземпляр [RagAppDatabase].
         */
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
