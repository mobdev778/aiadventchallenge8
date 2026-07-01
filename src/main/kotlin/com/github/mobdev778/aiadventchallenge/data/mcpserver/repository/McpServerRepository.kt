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

@Single
class McpServerRepository(
    private val mcpServerDao: McpServerDao,
) {

    fun observeServers(): Flow<List<McpServer>> =
        mcpServerDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()

    suspend fun getServer(serverId: UUID): McpServer? =
        mcpServerDao.observeAll()
            .map { entities -> entities.firstOrNull { it.id == serverId.toString() }?.toDomain() }
            .distinctUntilChanged()
            .first()

    suspend fun createServer(server: McpServer) {
        mcpServerDao.upsert(server.toEntity())
    }

    suspend fun updateServerActive(serverId: UUID, active: Boolean) {
        mcpServerDao.updateActive(id = serverId.toString(), active = active)
    }

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
