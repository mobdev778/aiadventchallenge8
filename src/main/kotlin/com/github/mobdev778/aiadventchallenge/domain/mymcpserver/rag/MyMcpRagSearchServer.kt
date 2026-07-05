package com.github.mobdev778.aiadventchallenge.domain.mymcpserver.rag

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagChatRepository
import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagDocumentRepository
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
import kotlinx.coroutines.flow.firstOrNull
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
    private val ragRussianFilter: RagRussianFilter,
    private val documentRepository: RagDocumentRepository,
    private val chatRepository: RagChatRepository,
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
                name = "my-mcp-rag-search-server",
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

        addTool(server)

        return server
    }

    private fun addTool(server: Server) {
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
    }

    private suspend fun executeVectorSearch(query: String): String {
        val query = ragRussianFilter.filter(query)

        println("!!! MyMCP Vector Search: executeVectorSearch(query='$query')")
        if (query.isBlank()) return Json.encodeToString(
            MyMcpRagSearchResponseDto.serializer(),
            MyMcpRagSearchResponseDto(status = "EMPTY_QUERY"),
        )

        val foundResults = try {
            val documents = documentRepository.observeDocuments().firstOrNull()
                ?: throw IllegalStateException("No documents found")
            val chats = chatRepository.observeChats().firstOrNull() ?: emptyList()
            val chatIds = chats.map { it.id }
            val documentId = documents.filter { !chatIds.contains(it.id) }.firstOrNull()?.id
                ?: throw IllegalStateException("Unable to find non-chat document")
            ragSearcher.search(documentId, query, 1)
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

    private fun textResult(text: String): CallToolResult =
        CallToolResult(
            content = listOf(TextContent(text = text)),
            isError = false,
        )
}
