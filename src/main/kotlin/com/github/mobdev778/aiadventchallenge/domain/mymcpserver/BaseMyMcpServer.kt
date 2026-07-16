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

/**
 * Абстрактная базовая реализация сервера MCP, использующая встроенный HTTP-сервер на основе Netty.
 * Предоставляет базовый жизненный цикл (запуск, остановка) и поток состояния сервера.
 *
 * Подклассы должны реализовать метод [createServer], определяющий логику MCP-сервера.
 *
 * @param name Название сервера.
 * @param description Описание сервера.
 * @param port Порт, на котором будет запущен HTTP-сервер.
 * @param launchAtStartup Флаг запуска сервера при старте приложения.
 */
abstract class BaseMyMcpServer(
    private val name: String,
    private val description: String,
    private val port: Int,
    val launchAtStartup: Boolean,
) : MyMcpServer {

    /**
     * Встроенный HTTP-сервер Netty. `null`, если сервер не запущен.
     */
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

    /**
     * Возвращает поток [MyMcpServerState], отражающий текущее состояние сервера и его изменения.
     * Состояние обновляется при запуске и остановке.
     *
     * @return холодный [Flow] с текущим и последующими значениями состояния.
     */
    override fun observeState(): Flow<MyMcpServerState> {
        return state
    }

    /**
     * Запускает HTTP-сервер и MCP-обработчик, если сервер ещё не запущен.
     * После успешного старта обновляет состояние, помечая сервер как работающий.
     */
    override suspend fun start() {
        if (engine != null) return

        val newEngine = embeddedServer(Netty, port = port) {
            configureMyMcpServer()
        }
        newEngine.start(wait = false)
        engine = newEngine
        state.update {
            it.copy(isRunning = true)
        }
    }

    /**
     * Останавливает HTTP-сервер с заданными периодами ожидания.
     * Обновляет состояние, сбрасывая флаг работы и очищая ссылку на движок.
     */
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

    /**
     * Создаёт экземпляр MCP-сервера, который будет зарегистрирован на HTTP-маршруте.
     * Вызывается при конфигурации приложения.
     *
     * @return экземпляр [Server] MCP.
     */
    protected abstract fun createServer(): Server

    /**
     * Константы для конфигурации остановки сервера.
     */
    private companion object {
        /**
         * Время ожидания (в миллисекундах) перед принудительной остановкой после запроса на остановку.
         */
        const val STOP_GRACE_PERIOD_MS = 2000L

        /**
         * Максимальное время ожидания (в миллисекундах) завершения остановки.
         */
        const val STOP_TIMEOUT_MS = 3000L
    }
}
