package com.github.mobdev778.aiadventchallenge.data.mcpserver.datasource

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.mobdev778.aiadventchallenge.data.mcpserver.datasource.model.McpServerEntity
import java.nio.file.Path

/**
 * Room-база данных для локального хранения информации об MCP-серверах.
 *
 * Класс описывает схему БД, содержит фабрику создания экземпляра с предварительно
 * настроенным SQLite-драйвером [BundledSQLiteDriver] и стратегией миграции
 * «разрушающей миграции» при несовместимости версий (сбрасывает все таблицы).
 *
 * Версия схемы: 3.
 *
 * @see McpServerEntity Сущность, представляющая строку таблицы `mcp_servers`.
 * @see McpServerDao DAO, предоставляющий операции чтения и записи.
 */
@Database(
    entities = [
        McpServerEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class McpServerAppDatabase : RoomDatabase() {

    /**
     * Возвращает реализацию [McpServerDao] для доступа к данным MCP-серверов.
     *
     * @return объект DAO, позволяющий наблюдать за списком серверов, добавлять,
     * обновлять, изменять активность и удалять записи.
     */
    abstract fun mcpServerDao(): McpServerDao

    companion object {
        /**
         * Создаёт и конфигурирует экземпляр базы данных [McpServerAppDatabase].
         *
         * Используется файловый путь [dbFile] для размещения файла БД, драйвер
         * [BundledSQLiteDriver] и миграция с полным сбросом таблиц при повышении
         * версии, если схема несовместима.
         *
         * @param dbFile абсолютный или относительный путь к файлу базы данных.
         * @return готовый к использованию объект RoomDatabase.
         */
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
