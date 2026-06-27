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
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.readText
import kotlin.streams.asSequence

class MyMcpScanFilesServer : BaseMyMcpServer(
    name = "MyMcpScanFilesServer",
    description = "Локальный MCP-сервер навигации по файлам проекта",
    port = 3000,
) {

    override fun createServer(): Server {
        val server = Server(
            serverInfo = Implementation(
                name = "my-mcp-server",
                version = "1.0.0",
            ),
            options = ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = false),
                ),
            ),
            instructions = "Local MCP server for project file inspection",
        )

        // Изменено: добавлен inputSchema с необязательным или обязательным параметром path
        server.addTool(
            name = "list_project_files",
            description = "Show files and directories strictly inside the specified directory (shallow list)",
            inputSchema = ToolSchema(
                properties = buildJsonObject {
                    put("path", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Relative or absolute directory path. Use empty string for root."))
                    })
                },
                required = emptyList() // Сделаем необязательным, чтобы по умолчанию смотрел корень
            ),
        ) { request: CallToolRequest ->
            val path = request.params.arguments?.get("path")?.jsonPrimitive?.content.orEmpty()
            textResult(listProjectFiles(path))
        }

        server.addTool(
            name = "find_files_by_name",
            description = "Find files by full or partial file name",
            inputSchema = ToolSchema(
                properties = buildJsonObject {
                    put("query", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Full or partial file name"))
                    })
                },
                required = listOf("query"),
            ),
        ) { request: CallToolRequest ->
            val query = request.params.arguments?.get("query")?.jsonPrimitive?.content.orEmpty()
            textResult(findFilesByName(query))
        }

        server.addTool(
            name = "read_project_file",
            description = "Read file content by relative or absolute path",
            inputSchema = ToolSchema(
                properties = buildJsonObject {
                    put("path", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Relative or absolute file path"))
                    })
                },
                required = listOf("path"),
            ),
        ) { request: CallToolRequest ->
            val path = request.params.arguments?.get("path")?.jsonPrimitive?.content.orEmpty()
            textResult(readProjectFile(path))
        }

        return server
    }

    // Изменено: теперь принимает pathValue и не использует Files.walk
    private fun listProjectFiles(pathValue: String): String {
        println("!!! MyMCP: listProjectFiles(${pathValue})")
        val root = projectRoot()

        // Разрешаем целевой путь аналогично методу чтения
        val requested = Paths.get(pathValue)
        val targetDir = if (requested.isAbsolute) requested.normalize() else root.resolve(requested).normalize()

        // Безопасность и валидация
        if (!targetDir.exists()) return "Directory not found: $pathValue"
        if (!targetDir.isDirectory()) return "Path is not a directory: $pathValue"
        if (!targetDir.startsWith(root)) return "Access denied: $pathValue"

        // Используем Files.list() вместо Files.walk(), чтобы получить строго 1 уровень глубины
        return try {
            Files.list(targetDir).use { paths ->
                paths.asSequence()
                    .map { file ->
                        val relativePath = root.relativize(file).toString()
                        // Добавляем косую черту для папок, чтобы LLM понимала структуру
                        if (file.isDirectory()) "$relativePath/" else relativePath
                    }
                    .sorted()
                    .toList()
                    .ifEmpty { listOf("<empty directory>") }
                    .joinToString(separator = "\n")
            }
        } catch (error: Exception) {
            "Failed to list directory: ${error.message.orEmpty()}"
        }
    }

    private fun findFilesByName(query: String): String {
        println("!!! MyMCP: findFilesByName: $query")
        if (query.isBlank()) return "No files found"

        val rootDir = projectRoot().toFile()
        val normalized = query.lowercase()

        // Вызываем вспомогательную рекурсивную функцию
        val results = searchRecursive(rootDir, rootDir, normalized)

        return if (results.size > 0) {
            results.joinToString("\n")
        } else {
            "No files found"
        }
    }

    private fun searchRecursive(currentDir: File, rootDir: File, query: String): List<String> {
        val foundFiles = mutableListOf<String>()
        val files = currentDir.listFiles() ?: return emptyList()

        for (file in files) {
            // Проверяем совпадение имени (без учета регистра)
            if (file.name.lowercase().contains(query)) {
                val relativePath = rootDir.toURI().relativize(file.toURI()).path
                foundFiles.add(relativePath)
            }

            // Если это папка, рекурсивно ищем внутри неё и объединяем результаты
            if (file.isDirectory) {
                val subDirResults = searchRecursive(file, rootDir, query)
                foundFiles.addAll(subDirResults)
            }
        }

        return foundFiles.sorted()
    }

    private fun readProjectFile(pathValue: String): String {
        println("!!! MyMCP: readProjectFile(${pathValue})")
        if (pathValue.isBlank()) return "Path is empty"
        val root = projectRoot()
        val requested = Paths.get(pathValue)
        val resolved = if (requested.isAbsolute) requested.normalize() else root.resolve(requested).normalize()
        if (!resolved.exists()) return "File not found: $pathValue"
        if (resolved.isDirectory()) return "Path is a directory: $pathValue"
        if (!resolved.startsWith(root)) return "Access denied: $pathValue"
        return try {
            resolved.readText(StandardCharsets.UTF_8)
        } catch (error: Exception) {
            "Failed to read file: ${error.message.orEmpty()}"
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
