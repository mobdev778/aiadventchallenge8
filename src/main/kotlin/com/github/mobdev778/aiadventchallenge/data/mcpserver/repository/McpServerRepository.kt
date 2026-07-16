package com.github.mobdev778.aiadventchallenge.data.mcpserver.repository

import com.github.mobdev778.aiadventchallenge.data.mcpserver.datasource.McpServerDao
import com.github.mobdev778.aiadventchallenge.data.mcpserver.datasource.model.McpServerEntity
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.model.McpServer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import java.util.UUID

/**
 * Репозиторий для работы с MCP-серверами.
 *
 * Обеспечивает операции наблюдения, получения по идентификатору, создания, изменения статуса активности
 * и удаления серверов. Использует [McpServerDao] в качестве источника данных и выполняет преобразование
 * между сущностями базы данных [McpServerEntity] и доменной моделью [McpServer].
 *
 * Зарегистрирован в Koin как синглтон.
 */
@Single
class McpServerRepository(
    private val mcpServerDao: McpServerDao,
) {

    /**
     * Возвращает поток, эмитирующий актуальный список всех MCP-серверов, отслеживая изменения в базе данных.
     * Элементы потока преобразуются из сущностей [McpServerEntity] в объекты [McpServer].
     * Повторяющиеся идентичные списки отфильтровываются при помощи [distinctUntilChanged].
     *
     * @return [Flow], который выдаёт [List] объектов [McpServer].
     */
    fun observeServers(): Flow<List<McpServer>> =
        mcpServerDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()

    /**
     * Приостанавливающая функция для получения конкретного MCP-сервера по его идентификатору.
     *
     * Дожидается первой эмиссии потока всех серверов и ищет в нём запись с заданным [serverId].
     * Если сервер найден, возвращает соответствующий [McpServer], иначе `null`.
     *
     * @param serverId уникальный идентификатор сервера.
     * @return объект [McpServer] или `null`, если сервер не найден.
     */
    suspend fun getServer(serverId: UUID): McpServer? =
        mcpServerDao.observeAll()
            .map { entities -> entities.firstOrNull { it.id == serverId.toString() }?.toDomain() }
            .distinctUntilChanged()
            .first()

    /**
     * Создаёт новый MCP-сервер или перезаписывает существующую запись при совпадении идентификатора.
     *
     * @param server объект [McpServer], который необходимо сохранить или обновить.
     */
    suspend fun createServer(server: McpServer) {
        mcpServerDao.upsert(server.toEntity())
    }

    /**
     * Обновляет флаг активности для MCP-сервера с указанным идентификатором.
     *
     * @param serverId уникальный идентификатор сервера.
     * @param active новое значение активности (`true` — активен, `false` — неактивен).
     */
    suspend fun updateServerActive(serverId: UUID, active: Boolean) {
        mcpServerDao.updateActive(id = serverId.toString(), active = active)
    }

    /**
     * Удаляет MCP-сервер из локального хранилища по его идентификатору.
     *
     * @param serverId уникальный идентификатор удаляемого сервера.
     */
    suspend fun deleteServer(serverId: UUID) {
        mcpServerDao.deleteById(id = serverId.toString())
    }

    private fun McpServerEntity.toDomain(): McpServer =
        McpServer(
            id = UUID.fromString(id),
            active = active,
            name = name,
            url = url,
            isLocal = false,
        )

    private fun McpServer.toEntity(): McpServerEntity =
        McpServerEntity(
            id = id.toString(),
            active = active,
            name = name,
            url = url,
        )
}
