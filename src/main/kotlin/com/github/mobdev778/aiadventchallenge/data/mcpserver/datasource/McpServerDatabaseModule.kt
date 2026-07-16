package com.github.mobdev778.aiadventchallenge.data.mcpserver.datasource

import com.intellij.openapi.application.PathManager
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.nio.file.Files
import java.nio.file.Path

/**
 * Модуль внедрения зависимостей для локальной базы данных MCP-серверов.
 *
 * Предоставляет singleton-зависимости [McpServerAppDatabase] и [McpServerDao] через Koin.
 * Настройка хранилища производится в подкаталоге `aiadventchallenge` системной директории IntelliJ IDEA,
 * что обеспечивает изоляцию данных плагина.
 */
@Module
class McpServerDatabaseModule {

    /**
     * Создаёт и настраивает единственный экземпляр базы данных Room для приложения.
     *
     * Формирует абсолютный путь к файлу БД как `{системная_директория}/aiadventchallenge/mcp_servers.db`,
     * при необходимости создавая родительские каталоги. Для открытия используется
     * деструктивная стратегия миграции (удаление таблиц при смене версии схемы).
     *
     * @return готовый к использованию объект [McpServerAppDatabase].
     */
    @Single
    fun provideMcpServerAppDatabase(): McpServerAppDatabase {
        val systemPath = PathManager.getSystemDir()
        val dir: Path = systemPath.resolve("aiadventchallenge")
        Files.createDirectories(dir)
        val dbFile = dir.resolve("mcp_servers.db")
        return McpServerAppDatabase.create(dbFile)
    }

    /**
     * Предоставляет реализацию [McpServerDao], полученную из переданной базы данных.
     *
     * @param mcpServerAppDatabase экземпляр базы данных [McpServerAppDatabase], из которого извлекается DAO.
     * @return DAO для операций с MCP-серверами.
     */
    @Single
    fun provideMcpServerDao(mcpServerAppDatabase: McpServerAppDatabase): McpServerDao {
        return mcpServerAppDatabase.mcpServerDao()
    }

}
