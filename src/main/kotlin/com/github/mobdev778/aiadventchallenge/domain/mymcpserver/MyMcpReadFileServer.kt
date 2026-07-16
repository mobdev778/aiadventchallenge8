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

/**
 * Локальный MCP-сервер, предоставляющий возможность чтения файлов из корневого каталога проекта.
 *
 * Наследуется от [BaseMyMcpServer] и добавляет инструмент `readFile`, позволяющий клиентам
 * запрашивать содержимое произвольного файла по относительному пути. При попытке чтения за пределами
 * корня проекта, несуществующего файла или директории возвращается пустая строка.
 *
 * @param name имя сервера
 * @param description описание сервера
 * @param port порт, на котором ожидается запуск (в текущей реализации может не использоваться)
 * @param launchAtStartup флаг автоматического запуска при старте приложения
 */
class MyMcpReadFileServer : BaseMyMcpServer(
    name = "MyMcpReadFileServer",
    description = "Локальный MCP-сервер чтения файлов проекта",
    port = 3000,
    launchAtStartup = false,
) {

    /**
     * Создаёт настроенный экземпляр [Server] с зарегистрированным инструментом `readFile`.
     *
     * Инструмент `readFile` принимает параметр `fileName` (относительный путь от корня проекта)
     * и возвращает содержимое файла в виде текста. Доступ ограничен файлами внутри корневого каталога.
     *
     * @return готовый к использованию экземпляр MCP-сервера.
     */
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
            description = "Reads a file by relative path from the project root " +
                "and returns its content as a single string",
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

        return when {
            !target.startsWith(root) -> ""
            !target.exists() -> ""
            target.isDirectory() -> ""
            else -> try {
                target.readText(StandardCharsets.UTF_8)
            } catch (_: Exception) {
                ""
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
