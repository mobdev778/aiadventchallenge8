package com.github.mobdev778.aiadventchallenge.domain.mymcpserver.rag

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.BaseMyMcpServer
import com.github.mobdev778.aiadventchallenge.domain.rag.RankedRagSearcher
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagFilterType
import com.github.mobdev778.aiadventchallenge.domain.rag.ranker.RankerFactory
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.annotation.Single

@Single
class MyMcpRagSearchServer(
    private val rankerFactory: RankerFactory,
    private val ragSearcher: RankedRagSearcher,
    private val chatClient: ChatClient,
    private val settingsRepository: SettingsRepository,
) : BaseMyMcpServer(
    name = "MyMcpRagSearchServer",
    description = "Локальный MCP-сервер семантического RAG-поиска по документам " +
            "с верификацией источников",
    port = 3004,
    launchAtStartup = true,
) {

    companion object {
        // Установите ваш порог релевантности (например, 0.65 для косинусного сходства)
        private const val RELEVANCE_THRESHOLD = 0.73f
    }

    override fun createServer(): Server {
        val server = Server(
            serverInfo = Implementation(
                name = "my-mcp-vector-document-searcher-server",
                version = "1.1.0",
            ),
            options = ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = false),
                ),
            ),
            // Усиление инструкций: жестко требуем от LLM проверять контекст и форматировать ответ
            instructions = "Local MCP server for semantic document search. " +
                    "CRITICAL RULES FOR THE ASSISTANT:\n" +
                    "1. ALWAYS provide a list of sources (source name and section/chunk_id) and exact quotes " +
                    "for every fact in your response.\n" +
                    "2. If the tool explicitly returns 'LOW_RELEVANCE' or an empty list of chunks, " +
                    "or if the found chunks do not contain the answer, you MUST strictly reply: " +
                    "'Я не знаю ответа на этот вопрос, так как в предоставленных документах " +
                    "нет релевантной информации.' and ask the user for clarification.\n" +
                    "3. Never invent facts outside the provided 'text' fragments.",
        )

        server.addTool(
            name = "searchKnowledgeBase",
            description = "Searches the document knowledge base using semantic/vector search. " +
                    "Returns chunks with sources, sections, and relevance scores.",
            inputSchema = ToolSchema(
                properties = buildJsonObject {
                    put("query", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put(
                            "description", JsonPrimitive(
                                "The natural language search query or question to look up in the documents"
                            )
                        )
                    })
                },
                required = listOf("query"),
            ),
        ) { request: CallToolRequest ->
            val query = request.params.arguments?.get("query")?.jsonPrimitive?.content.orEmpty()
            runBlocking(Dispatchers.Default) {
                textResult(executeVectorSearch(query))
            }
        }

        return server
    }

    private suspend fun executeVectorSearch(query: String): String {
        val query = filterRussian(query)

        println("!!! MyMCP Vector Search: executeVectorSearch(query='$query')")
        if (query.isBlank()) return Json.encodeToString(
            MyMcpRagSearchResponseDto.serializer(),
            MyMcpRagSearchResponseDto(status = "EMPTY_QUERY"),
        )

        val foundResults = try {
            ragSearcher.search(query)
        } catch (e: Exception) {
            println("!!! MyMCP Error during vector search: ${e.message}")
            emptyList()
        }

        // 1. Фильтруем результаты по порогу релевантности
        val finalRanker = rankerFactory.create(RagFilterType.Similarity)
        finalRanker.init(query)
        var minScore = 1.0

        val filteredResults = foundResults.filter {
            val score = finalRanker.rank(it.text, it.vector)
            minScore = Math.min(minScore, score)
            println("!!! minScore: $minScore")
            score >= RELEVANCE_THRESHOLD
        }

        val response = if (filteredResults.isEmpty()) {
            // 2. Если ничего не нашли выше порога — возвращаем специальный маркер для LLM
            MyMcpRagSearchResponseDto(
                status = "LOW_RELEVANCE",
                message = "No documents matched the query above the required relevance threshold ($RELEVANCE_THRESHOLD). Min score: $minScore",
                chunks = emptyList(),
            )
        } else {
            // 3. Формируем валидный структурированный JSON-ответ с цитатами и метаданными
            MyMcpRagSearchResponseDto(
                status = "SUCCESS",
                chunks = filteredResults.map { result ->
                    MyMcpRagSearchChunkDto(
                        source = result.source,
                        section = result.section,
                        text = result.text,
                        relevance_score = result.score,
                    )
                },
            )
        }

        return Json.encodeToString(MyMcpRagSearchResponseDto.serializer(), response)
    }


    private suspend fun filterRussian(query: String): String {
        return if (hasRussianLetters(query)) {
            val settings = settingsRepository.getSettings()
            val response = chatClient.execute(
                request = ChatRequest(
                    model = settings.baseModel,
                    messages = listOf(
                        Message(
                            role = Role.System,
                            content = "Translate user prompt into English",
                        ),
                        Message(
                            role = Role.User,
                            content = query
                        )
                    )
                )
            )
            response.choices.firstOrNull()?.message?.content ?: query
        } else {
            query
        }
    }

    private val RUSSIAN_LETTERS = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя".toSet()

    private fun hasRussianLetters(query: String): Boolean {
        for (c in query) {
            if (RUSSIAN_LETTERS.contains(c)) {
                return true
            }
        }
        return false
    }

    private fun textResult(text: String): CallToolResult =
        CallToolResult(
            content = listOf(TextContent(text = text)),
            isError = false,
        )
}
