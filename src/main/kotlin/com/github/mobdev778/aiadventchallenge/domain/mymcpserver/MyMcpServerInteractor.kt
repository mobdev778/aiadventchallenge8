package com.github.mobdev778.aiadventchallenge.domain.mymcpserver

import com.github.mobdev778.aiadventchallenge.domain.mcpserver.model.McpServer
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.model.MyMcpServerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single
import java.nio.charset.StandardCharsets
import java.util.UUID

@Single
class MyMcpServerInteractor(
    scope: CoroutineScope,
    ragServer: MyMcpRagSearchServer,
) {

    private val servers: List<MyMcpServer> = listOf(
        MyMcpReadFileServer(),
        MyMcpCalcPrimeNumbersServer(scope),
        MyMcpServerSaveToFileServer(),
        ragServer,
    )

    val localServersFlow: Flow<List<McpServer>> = combine(
        servers.map {
            it.observeState()
        }
    ) {
        it.map { server ->
            McpServer(
                id = UUID.nameUUIDFromBytes(server.url.toByteArray(StandardCharsets.UTF_8)),
                active = server.isRunning,
                name = server.name,
                url = server.url,
                isLocal =  true,
            )
        }.toList()
    }

    val serverStatesFlow: Flow<List<MyMcpServerState>> = combine(
        servers.map { it.observeState() }
    ) {
        it.toList()
    }

    suspend fun start(name: String) {
        val server = servers.firstOrNull { it.observeState().first().name == name }
        server?.start()
    }

    suspend fun stop(name: String) {
        val server = servers.firstOrNull { it.observeState().first().name == name }
        server?.stop()
    }
}

