package com.github.mobdev778.aiadventchallenge.domain.mymcpserver

import com.github.mobdev778.aiadventchallenge.data.mymcpserver.repository.MyMcpServerConfigRepository
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.model.MyMcpServer
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.model.MyMcpServerConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
class MyMcpServerInteractor(
    private val configRepository: MyMcpServerConfigRepository,
    private val myMcpNettyServer: MyMcpNettyServer,
    private val scope: CoroutineScope,
) {

    private val serverState = MutableStateFlow(MyMcpServer(isRunning = false, url = ""))

    init {
        scope.launch(Dispatchers.IO) {
            configRepository.observeConfig().collect { config ->
                if (!serverState.value.isRunning) {
                    serverState.value = serverState.value.copy(url = buildUrl(config.port))
                }
            }
        }

        scope.launch(Dispatchers.IO) {
            configRepository.observeConfig().distinctUntilChanged().collect { current ->
                if (current.launchAtStartup && !serverState.value.isRunning) {
                    start(current)
                }
            }
        }
    }

    fun observeConfig(): Flow<MyMcpServerConfig> = configRepository.observeConfig()

    fun observeServer(): StateFlow<MyMcpServer> = serverState.asStateFlow()

    suspend fun saveConfig(config: MyMcpServerConfig) {
        val wasRunning = serverState.value.isRunning
        if (wasRunning) {
            stop()
        }
        configRepository.save(config)
        if (wasRunning || config.launchAtStartup) {
            start(config)
        } else {
            serverState.value = serverState.value.copy(url = buildUrl(config.port))
        }
    }

    suspend fun start() {
        start(configRepository.observeConfig().first())
    }

    suspend fun stop() {
        myMcpNettyServer.stop()
        serverState.value = serverState.value.copy(isRunning = false)
    }

    private suspend fun start(config: MyMcpServerConfig) {
        if (serverState.value.isRunning) return

        runCatching {
            myMcpNettyServer.start(config.port)
        }.onSuccess {
            serverState.value = MyMcpServer(
                isRunning = true,
                url = buildUrl(config.port),
            )
        }.onFailure {
            serverState.value = MyMcpServer(
                isRunning = false,
                url = buildUrl(config.port),
            )
            throw it
        }
    }

    private fun buildUrl(port: Int): String = "http://localhost:$port/mcp"
}
