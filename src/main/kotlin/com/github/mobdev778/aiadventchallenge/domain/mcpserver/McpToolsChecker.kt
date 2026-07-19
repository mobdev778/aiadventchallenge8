package com.github.mobdev778.aiadventchallenge.domain.mcpserver

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.sse.SSE
import io.modelcontextprotocol.kotlin.sdk.client.mcpStreamableHttp
import io.modelcontextprotocol.kotlin.sdk.types.ListToolsRequest
import io.modelcontextprotocol.kotlin.sdk.types.Tool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

/**
 * Инструмент для проверки доступности и получения списка инструментов MCP-сервера.
 *
 * Класс отвечает за HTTP-взаимодействие с сервером по протоколу MCP Streamable HTTP
 * с использованием Ktor. Создаётся как singleton-компонент DI (Koin) и предоставляет
 * единственную публичную suspend-функцию для извлечения списка инструментов.
 */
@Single
class McpToolsChecker {

    /**
     * Загружает список инструментов с заданного MCP-сервера.
     *
     * Производит нормализацию URL (удаление висящего `/`), проверку его непустоты,
     * подключается по HTTP и использует MCP-клиент для получения списка инструментов.
     * Взаимодействие реализовано с поддержкой Server-Sent Events (SSE). Выполнение
     * переносится в диспетчер [Dispatchers.IO].
     *
     * @param url базовый адрес MCP-сервера. Он нормализуется внутри функции.
     * @return список инструментов [Tool], зарегистрированных на сервере.
     * @throws IllegalArgumentException если после нормализации URL оказался пустым или состоит из пробелов.
     * @throws IllegalStateException если произошла ошибка соединения или любая другая нештатная ситуация,
     *         с локализованным читаемым сообщением, содержащим детали корневой причины.
     */
    @Suppress("MagicNumber", "TooGenericExceptionCaught")
    suspend fun loadTools(url: String): List<Tool> = withContext(Dispatchers.IO) {
        val normalizedUrl = url.trim().removeSuffix("/")
        require(normalizedUrl.isNotEmpty()) { "MCP server URL must not be blank" }

        val httpClient = HttpClient(CIO) {
            install(SSE)
            install(HttpTimeout) {
                requestTimeoutMillis = 300_000
                connectTimeoutMillis = 300_000
                socketTimeoutMillis = 300_000
            }
        }

        try {
            val client = httpClient.mcpStreamableHttp(normalizedUrl) {
                headers["Accept"] = "application/json, text/event-stream"
            }
            client.listTools(ListToolsRequest()).tools
        } catch (error: Throwable) {
            throw IllegalStateException(error.toReadableMessage(normalizedUrl), error)
        } finally {
            httpClient.close()
        }
    }

    private fun Throwable.toReadableMessage(url: String): String {
        val rootCause = generateSequence(this) { it.cause }.last()
        val rootMessage = rootCause.message?.takeIf { it.isNotBlank() }
        val rootType = rootCause::class.qualifiedName.orEmpty()

        return when {
            this is IllegalArgumentException && message?.contains("Failed to prepare request") == true ->
                "Не удалось подготовить HTTP-запрос к $url. Вероятна несовместимость версий Ktor в classpath."

            rootType.contains("HttpTimeout") ->
                "Таймаут при подключении к $url"

            rootMessage != null -> "Ошибка подключения к $url: $rootMessage"
            else -> "Ошибка подключения к $url: ${rootCause::class.simpleName ?: "Unknown error"}"
        }
    }
}
