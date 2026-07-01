package com.github.mobdev778.aiadventchallenge.domain.mymcpserver

import com.github.mobdev778.aiadventchallenge.domain.rag.RagSearcher
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
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.annotation.Single

@Single
class MyMcpRagSearchServer(
    private val ragSearcher: RagSearcher
) : BaseMyMcpServer(
    name = "MyMcpRagSearchServer",
    description = "Локальный MCP-сервер семантического RAG-поиска по документам",
    port = 3004,
    launchAtStartup = true,
) {

    override fun createServer(): Server {
        val server = Server(
            serverInfo = Implementation(
                name = "my-mcp-vector-document-searcher-server",
                version = "1.0.0",
            ),
            options = ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = false),
                ),
            ),
            instructions = "Local MCP server for semantic document search using vector embeddings. Use this tool when you need factual information from the uploaded documents to answer user questions.",
        )

        // Регистрируем инструмент семантического поиска
        server.addTool(
            name = "searchKnowledgeBase",
            description = "Searches the document knowledge base using semantic/vector search. Input a clear, descriptive natural language query.",
            inputSchema = ToolSchema(
                properties = buildJsonObject {
                    put("query", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("The natural language search query or question to look up in the documents"))
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
        println("!!! MyMCP Vector Search: executeVectorSearch(query='$query')")
        if (query.isBlank()) return "[]"

        // Вызываем ваш RAG-движок, который превратит query в эмбеддинг и найдет top-3 абзаца
        val foundParagraphs = try {
            ragSearcher.search(query)
        } catch (e: Exception) {
            println("!!! MyMCP Error during vector search: ${e.message}")
            return "[]"
        }

        // Сериализуем список найденных абзацев в JSON-массив строк
        return foundParagraphs.joinToString(
            prefix = "[",
            postfix = "]",
            separator = ", ",
        ) { paragraph ->
            buildString {
                append('"')
                append(paragraph.escapeForJson())
                append('"')
            }
        }
    }

    private fun String.escapeForJson(): String {
        return Pair(this, " ").first // Защита от пустых строк
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    private fun textResult(text: String): CallToolResult =
        CallToolResult(
            content = listOf(TextContent(text = text)),
            isError = false,
        )
}
