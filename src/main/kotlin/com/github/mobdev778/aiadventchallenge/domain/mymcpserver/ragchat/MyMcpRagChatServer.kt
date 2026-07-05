package com.github.mobdev778.aiadventchallenge.domain.mymcpserver.ragchat

import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagChatMessageDto
import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagChatRepository
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.BaseMyMcpServer
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class MyMcpRagChatServer(
    private val ragChatRepository: RagChatRepository,
) : BaseMyMcpServer(
    name = "MyMcpRagChatServer",
    description = "Локальный MCP-сервер семантического RAG-поиска по сообщениям чата " +
            "с верификацией источников",
    port = 3005,
    launchAtStartup = true,
) {

    override fun createServer(): Server {
        val server = Server(
            serverInfo = Implementation(
                name = "my-mcp-rag-chat-server",
                version = "1.0.0",
            ),
            options = ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = false),
                ),
            ),
            instructions = "Local MCP server for semantic chat message search. " +
                    "CRITICAL RULES FOR THE ASSISTANT:\n" +
                    "1. Use this tool to search for relevant messages in a specific chat by chatId.\n" +
                    "2. If the tool returns an empty list or NO_RESULTS status, " +
                    "respond that no relevant messages were found in the chat.\n" +
                    "3. Always reference the time and role of found messages when presenting results.",
        )

        addTool(server)

        return server
    }

    private fun addTool(server: Server) {
        server.addTool(
            name = "searchChatMessages",
            description = "Searches chat messages using semantic/vector search within a specific chat. " +
                    "Returns relevant messages with their text, role, and timestamp.",
            inputSchema = ToolSchema(
                properties = buildJsonObject {
                    put("chatId", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put(
                            "description", JsonPrimitive(
                                "The UUID of the chat to search messages in"
                            )
                        )
                    })
                    put("query", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put(
                            "description", JsonPrimitive(
                                "The natural language search query to find relevant messages"
                            )
                        )
                    })
                },
                required = listOf("chatId", "query"),
            ),
        ) { request: CallToolRequest ->
            val chatIdStr = request.params.arguments?.get("chatId")?.jsonPrimitive?.content.orEmpty()
            val query = request.params.arguments?.get("query")?.jsonPrimitive?.content.orEmpty()
            runBlocking(Dispatchers.Default) {
                textResult(executeChatSearch(chatIdStr, query))
            }
        }
    }

    private suspend fun executeChatSearch(chatIdStr: String, query: String): String {
        println("!!! MyMCP Chat Search: executeChatSearch(chatId='$chatIdStr', query='$query')")

        if (chatIdStr.isBlank() || query.isBlank()) {
            return Json.encodeToString(
                MyMcpRagChatSearchResponseDto.serializer(),
                MyMcpRagChatSearchResponseDto(
                    status = if (chatIdStr.isBlank()) "EMPTY_CHAT_ID" else "EMPTY_QUERY",
                    messages = emptyList(),
                )
            )
        }

        val chatId = try {
            UUID.fromString(chatIdStr)
        } catch (e: IllegalArgumentException) {
            return Json.encodeToString(
                MyMcpRagChatSearchResponseDto.serializer(),
                MyMcpRagChatSearchResponseDto(
                    status = "INVALID_CHAT_ID",
                    message = "Invalid chat ID format: $chatIdStr",
                    messages = emptyList(),
                )
            )
        }

        val foundMessages: List<RagChatMessageDto> = try {
            ragChatRepository.find(chatId, query)
        } catch (e: Exception) {
            println("!!! MyMCP Error during chat search: ${e.message}")
            emptyList()
        }

        val response = if (foundMessages.isEmpty()) {
            MyMcpRagChatSearchResponseDto(
                status = "NO_RESULTS",
                message = "No messages matched the query in chat $chatId",
                messages = emptyList(),
            )
        } else {
            MyMcpRagChatSearchResponseDto(
                status = "SUCCESS",
                messages = foundMessages.map { msg ->
                    MyMcpRagChatMessageDto(
                        text = msg.text,
                        time = msg.time,
                        role = msg.role,
                    )
                },
            )
        }

        return Json.encodeToString(MyMcpRagChatSearchResponseDto.serializer(), response)
    }

    private fun textResult(text: String): CallToolResult =
        CallToolResult(
            content = listOf(TextContent(text = text)),
            isError = false,
        )
}

@Serializable
data class MyMcpRagChatSearchResponseDto(
    val status: String,
    val message: String? = null,
    val messages: List<MyMcpRagChatMessageDto>,
)

@Serializable
data class MyMcpRagChatMessageDto(
    val text: String,
    val time: Long,
    val role: String,
)
