package com.github.mobdev778.aiadventchallenge.domain.mcpserver

import com.github.mobdev778.aiadventchallenge.data.mcpserver.repository.McpServerRepository
import com.github.mobdev778.aiadventchallenge.domain.agent.model.ToolResponse
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ToolCall
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.model.McpServer
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.MyMcpServerInteractor
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.sse.SSE
import io.modelcontextprotocol.kotlin.sdk.client.Client
import io.modelcontextprotocol.kotlin.sdk.client.mcpStreamableHttp
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import io.modelcontextprotocol.kotlin.sdk.types.Tool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.annotation.Single
import java.util.UUID

/**
 * Основной интерактор для работы с MCP-серверами (Model Context Protocol).
 *
 * Объединяет управление удалёнными и локальными серверами, предоставляет реактивные потоки
 * списков серверов и доступных инструментов, а также выполняет вызовы инструментов от имени
 * AI-агента. Использует [CachedMcpToolsChecker] для кэширования списка инструментов,
 * [McpServerRepository] для работы с хранилищем удалённых серверов и [MyMcpServerInteractor]
 * для локальных серверов.
 *
 * @property mcpServerRepository репозиторий удалённых MCP-серверов.
 * @property myMcpServerInteractor интерактор локальных MCP-серверов.
 * @property cachedChecker кэширующий загрузчик инструментов.
 * @property scope корутин-скоуп для асинхронных операций.
 */
@Single
class McpServerInteractor(
    private val mcpServerRepository: McpServerRepository,
    private val myMcpServerInteractor: MyMcpServerInteractor,
    private val cachedChecker: CachedMcpToolsChecker,
    private val scope: CoroutineScope,
) {

    /**
     * Маппинг имени инструмента на URL MCP-сервера, с которого этот инструмент был загружен.
     * Заполняется автоматически при загрузке инструментов в [activeToolsFlow].
     */
    val toolUrlMap = HashMap<String, String>()

    /**
     * Реактивный поток, эмитирующий актуальный список всех MCP-серверов (удалённых и локальных).
     * Объединяет потоки [McpServerRepository.observeServers] и [MyMcpServerInteractor.localServersFlow]
     * с помощью [combine], отфильтровывая дублирующиеся эмиссии.
     */
    val allServersFlow: Flow<List<McpServer>> = combine(
        mcpServerRepository.observeServers(),
        myMcpServerInteractor.localServersFlow,
    ) { remoteServers, localServers ->
        remoteServers + localServers
    }.distinctUntilChanged()

    /**
     * Поток, предоставляющий полный список активных инструментов со всех доступных серверов.
     * Загружает инструменты параллельно для каждого активного сервера, используя [CachedMcpToolsChecker.loadTools].
     * Одновременно заполняет [toolUrlMap] для последующего использования в [sendRequest].
     * В случае ошибки загрузки инструментов для конкретного сервера, он пропускается (возвращается пустой список).
     */
    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    val activeToolsFlow : Flow<List<Tool>> = allServersFlow
        .map { servers ->
            val jobs = servers
                .filter { it.active }
                .map { server ->
                    scope.async {
                        try {
                            val tools = cachedChecker.loadTools(server.url)
                            tools.forEach { toolUrlMap[it.name] = server.url }
                            tools
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }
                }
            val lists: List<List<Tool>> = jobs.awaitAll()
            lists.flatten()
        }

    /**
     * Отправляет вызов инструмента на соответствующий MCP-сервер и возвращает ответ [ToolResponse].
     *
     * Определяет URL сервера по имени инструмента через [toolUrlMap]. Если URL не найден, возвращает `null`.
     * Создаёт HTTP-клиент с поддержкой Server-Sent Events (SSE), инициирует соединение с сервером через
     * MCP-протокол и делегирует выполнение приватному методу [sendRequest].
     *
     * @param toolCall информация о вызове инструмента, включая имя и аргументы.
     * @return [ToolResponse] с результатом выполнения инструмента, или `null`, если сервер не определён.
     */
    @Suppress("MagicNumber")
    suspend fun sendRequest(toolCall: ToolCall): ToolResponse? {
        println("!!! sendRequest($toolCall)")

        val url = toolUrlMap[toolCall.function.name] ?: return null
        val normalizedUrl = url.trim().removeSuffix("/")
        require(normalizedUrl.isNotEmpty()) { "MCP server URL must not be blank" }

        val httpClient = HttpClient(CIO) {
            install(SSE)
            install(HttpTimeout) {
                requestTimeoutMillis = 600_000
                connectTimeoutMillis = 600_000
                socketTimeoutMillis = 600_000
            }
        }

        val result = httpClient.use { httpClient ->
            val client = httpClient.mcpStreamableHttp(normalizedUrl) {
                headers["Accept"] = "application/json, text/event-stream"
            }
            sendRequest(client, toolCall)
        }
        return result
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private suspend fun sendRequest(client: Client, toolCall: ToolCall): ToolResponse? {
        println("sendRequest($client, $toolCall)")

        // 1. Десериализуем строку аргументов от OpenAI в JsonObject, который требует MCP SDK
        val mcpArguments: JsonObject = try {
            Json.parseToJsonElement(toolCall.function.arguments).jsonObject
        } catch (e: Exception) {
            // Защита на случай, если LLM прислала невалидный JSON
            JsonObject(emptyMap())
        }

        // 2. Делаем вызов к MCP серверу через SDK
        // В зависимости от версии SDK, аргументы передаются либо вторым параметром, либо через объект CallToolRequest
        val result: CallToolResult = client.callTool(
            name = toolCall.function.name,
            arguments = mcpArguments
        )

        // 3. Вытаскиваем текстовый ответ из контента, который вернул MCP-сервер.
        // Сервер может возвращать массив элементов (текст, изображения и т.д.). Собираем весь текст в одну строку.
        val mergedContent = result.content.joinToString(separator = "\n") { contentElement ->
            when (contentElement) {
                is TextContent -> contentElement.text
                else -> "" // Игнорируем или обрабатываем другие типы (например, ImageContent), если нужно
            }
        }

        println("!!! merged content: $mergedContent")

        // 4. Мапим результат в твой чистый ToolResponse для OpenAI истории
        return ToolResponse(
            toolCallId = toolCall.id,
            name = toolCall.function.name,
            content = mergedContent
        )
    }

    /**
     * Изменяет флаг активности для указанного MCP-сервера.
     *
     * @param serverId уникальный идентификатор сервера.
     * @param active новое значение активности (`true` — активен, `false` — неактивен).
     */
    suspend fun updateServerActive(serverId: UUID, active: Boolean) {
        mcpServerRepository.updateServerActive(serverId, active)
    }

    /**
     * Удаляет MCP-сервер по его идентификатору.
     *
     * @param serverId уникальный идентификатор удаляемого сервера.
     */
    suspend fun deleteServer(serverId: UUID) {
        mcpServerRepository.deleteServer(serverId = serverId)
    }
}
