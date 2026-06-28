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
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.readText

class MyMcpReadFileServer : BaseMyMcpServer(
    name = "MyMcpReadFileServer",
    description = "Локальный MCP-сервер чтения файлов проекта",
    port = 3000,
    launchAtStartup = true,
) {

    override fun createServer(): Server {
        val server = Server(
            serverInfo = Implementation(
                name = "my-mcp-read-file-server",
                version = "1.0.0",
            ),
            options = ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = false),
                ),
            ),
            instructions = "Local MCP server for reading files from the project root",
        )

        server.addTool(
            name = "readFile",
            description = "Reads a file by relative path from the project root and returns its content as a single string",
            inputSchema = ToolSchema(
                properties = buildJsonObject {
                    put("fileName", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Relative file path from the project root"))
                    })
                },
                required = listOf("fileName"),
            ),
        ) { request: CallToolRequest ->
            val fileName = request.params.arguments?.get("fileName")?.jsonPrimitive?.content.orEmpty()
            textResult(readFile(fileName))
        }

        return server
    }

    private fun readFile(fileName: String): String {
        println("!!! MyMCP: readFile($fileName)")
        if (fileName.isBlank()) return ""

        val root = projectRoot()
        val target = root.resolve(fileName).normalize()

        if (!target.startsWith(root)) return ""
        if (!target.exists()) return ""
        if (target.isDirectory()) return ""

        return try {
            target.readText(StandardCharsets.UTF_8)
        } catch (_: Exception) {
            ""
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
