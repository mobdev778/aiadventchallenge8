package com.github.mobdev778.aiadventchallenge.domain.mymcpserver

import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.model.MyMcpServerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single

@Single
class MyMcpServerInteractor(
    scope: CoroutineScope,
) {

    private val servers: List<MyMcpServer> = listOf(
        MyMcpScanFilesServer(),
        MyMcpCalcPrimeNumbersServer(scope),
    )

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
