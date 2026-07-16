package com.github.mobdev778.aiadventchallenge.data.mcpserver.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.mobdev778.aiadventchallenge.data.mcpserver.datasource.model.McpServerEntity
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс DAO (Data Access Object) для работы с таблицей MCP-серверов в локальной базе данных.
 *
 * Предоставляет операции наблюдения за списком серверов, вставки/обновления, изменения статуса
 * активности и удаления записей. Использует Room для генерации реализации.
 */
@Dao
interface McpServerDao {

    /**
     * Возвращает поток, эмитирующий список всех MCP-серверов, отсортированных по имени в алфавитном
     * порядке без учёта регистра. Поток обновляется при любых изменениях в таблице.
     *
     * @return [Flow], выдающий текущий список сущностей [McpServerEntity].
     */
    @Query("SELECT * FROM mcp_servers ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<McpServerEntity>>

    /**
     * Вставляет новую или заменяет существующую запись о MCP-сервере. Конфликт по первичному ключу
     * разрешается стратегией [OnConflictStrategy.REPLACE].
     *
     * @param entity сущность [McpServerEntity], которую необходимо сохранить или обновить.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: McpServerEntity)

    /**
     * Обновляет флаг активности для указанного MCP-сервера.
     *
     * @param id уникальный идентификатор сервера.
     * @param active новое состояние активности (true — активен, false — неактивен).
     */
    @Query("UPDATE mcp_servers SET active = :active WHERE id = :id")
    suspend fun updateActive(id: String, active: Boolean)

    /**
     * Удаляет запись о MCP-сервере по его идентификатору.
     *
     * @param id уникальный идентификатор сервера, подлежащего удалению.
     */
    @Query("DELETE FROM mcp_servers WHERE id = :id")
    suspend fun deleteById(id: String)
}
