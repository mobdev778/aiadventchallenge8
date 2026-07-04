package com.github.mobdev778.aiadventchallenge.domain.mymcpserver

import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import kotlin.io.path.writeText

class MyMcpServerSaveToFileServer : BaseMyMcpServer(
    name = "MyMcpServerSaveToFileServer",
    description = "Локальный MCP-сервер сохранения Markdown-файлов в корень проекта",
    port = 3002,
    launchAtStartup = true,
) {

    override fun createServer(): Server {
        val server = Server(
            serverInfo = Implementation(
                name = "my-mcp-save-to-file-server",
                version = "1.0.0",
            ),
            options = ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = false),
                ),
            ),
            instructions = "Local MCP server for saving markdown files into the project root",
        )

        server.addTool(
            name = "saveToFile",
            description = "Saves provided Markdown text into a file in the project root",
            inputSchema = ToolSchema(
                properties = buildJsonObject {
                    put("fileName", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("File name to create in the project root"))
                    })
                    put("markdown", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Markdown text to save"))
                    })
                },
                required = listOf("fileName", "markdown"),
            ),
        ) { request: CallToolRequest ->
            val fileName = request.params.arguments?.get("fileName")?.jsonPrimitive?.content.orEmpty()
            val markdown = request.params.arguments?.get("markdown")?.jsonPrimitive?.content.orEmpty()
            textResult(saveToFile(fileName, markdown))
        }

        return server
    }

    private fun saveToFile(fileName: String, markdown: String): String {
        println("!!! MyMCP: saveToFile($fileName)")
        if (fileName.isBlank()) return "File name is empty"

        val root = projectRoot()
        val target = root.resolve(fileName).normalize()

        return when {
            !target.startsWith(root) -> "Access denied: $fileName"
            target.fileName?.toString() != fileName -> "Only file names in the project root are allowed: $fileName"
            else -> try {
                target.writeText(markdown, StandardCharsets.UTF_8)
                "Saved to ${target.fileName}"
            } catch (error: Exception) {
                "Failed to save file: ${error.message.orEmpty()}"
            }
        }
    }

    private fun textResult(text: String): CallToolResult =
        CallToolResult(
            content = listOf(TextContent(text = text)),
            isError = false,
        )

    private fun projectRoot(): Path = File("/home/ruslan/AI/AIAdventChallenge8/GitHub/aiadventchallenge8")
        .toPath()
}
