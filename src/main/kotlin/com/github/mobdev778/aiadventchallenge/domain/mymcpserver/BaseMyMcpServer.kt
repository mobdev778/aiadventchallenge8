package com.github.mobdev778.aiadventchallenge.domain.mymcpserver

import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.model.MyMcpServerState
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.mcpStreamableHttp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.String

abstract class BaseMyMcpServer(
    private val name: String,
    private val description: String,
    private val port: Int,
    val launchAtStartup: Boolean,
) : MyMcpServer {

    protected var engine: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null

    private val state = MutableStateFlow(
        MyMcpServerState(
            name = name,
            description = description,
            isRunning = false,
            url = "http://localhost:$port/mcp",
            launchAtStartup = launchAtStartup,
        )
    )

    override fun observeState(): Flow<MyMcpServerState> {
        return state
    }

    override suspend fun start() {
        if (engine != null) return

        val newEngine = embeddedServer(Netty, port = port, host = "localhost") {
            configureMyMcpServer()
        }
        newEngine.start(wait = false)
        engine = newEngine
        state.update {
            it.copy(isRunning = true)
        }
    }

    override suspend fun stop() {
        engine?.stop(STOP_GRACE_PERIOD_MS, STOP_TIMEOUT_MS)
        engine = null
        state.update {
            it.copy(isRunning = false)
        }
    }

    private fun Application.configureMyMcpServer() {
        install(ContentNegotiation) {
            json()
        }

        mcpStreamableHttp("/mcp") {
            createServer()
        }
    }

    protected abstract fun createServer(): Server

    private companion object {
        const val STOP_GRACE_PERIOD_MS = 2000L
        const val STOP_TIMEOUT_MS = 3000L
    }
}
