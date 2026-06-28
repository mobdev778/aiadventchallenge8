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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.zip.ZipInputStream

class MyMcpServerMatrixQuoteSearcherServer : BaseMyMcpServer(
    name = "MyMcpServerMatrixQuoteSearcherServer",
    description = "Локальный MCP-сервер поиска цитат из фильма Матрица",
    port = 3003,
    launchAtStartup = true,
) {

    private val lines: List<String> = loadLines()
    private val wordToLineIndexes: HashMap<String, List<Int>> = buildWordToLineIndexes(lines)

    override fun createServer(): Server {
        val server = Server(
            serverInfo = Implementation(
                name = "my-mcp-matrix-quote-searcher-server",
                version = "1.0.0",
            ),
            options = ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = false),
                ),
            ),
            instructions = "Local MCP server for searching Matrix movie quotes by word",
        )

        server.addTool(
            name = "findQuote",
            description = "Finds Matrix movie quotes containing the provided word",
            inputSchema = ToolSchema(
                properties = buildJsonObject {
                    put("word", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Word to search in Matrix quotes"))
                    })
                },
                required = listOf("word"),
            ),
        ) { request: CallToolRequest ->
            val word = request.params.arguments?.get("word")?.jsonPrimitive?.content.orEmpty()
            textResult(findQuote(word))
        }

        return server
    }

    private fun findQuote(word: String): String {
        println("!!! MyMCP: findQuote($word)")
        if (word.isBlank()) return "[]"

        val normalizedWord = normalizeWord(word)
        if (normalizedWord.isBlank()) return "[]"

        val indexes = wordToLineIndexes[normalizedWord].orEmpty()
        val quotes = indexes.mapNotNull {
            buildQuote(it)
        }

        return quotes.joinToString(
            prefix = "[",
            postfix = "]",
            separator = ", ",
        ) { quote ->
            buildString {
                append('"')
                append(quote.escapeForJson())
                append('"')
            }
        }
    }

    private fun loadLines(): List<String> {
        val resourceStream = javaClass.classLoader.getResourceAsStream("matrix.zip")
            ?: return emptyList()

        return resourceStream.use { inputStream ->
            ZipInputStream(inputStream).use { zipInputStream ->
                val entry = zipInputStream.nextEntry ?: return@use emptyList()
                if (entry.isDirectory) return@use emptyList()

                BufferedReader(InputStreamReader(zipInputStream, StandardCharsets.UTF_8)).use { reader ->
                    reader.readLines()
                        .map { it.replace("\t", "    ") }
                        .filter { it.isNotEmpty() }
                }
            }
        }
    }

    private fun buildWordToLineIndexes(lines: List<String>): HashMap<String, List<Int>> {
        val mutableIndex = HashMap<String, MutableList<Int>>()

        lines.forEachIndexed { index, line ->
            extractWords(line)
                .distinct()
                .forEach { word ->
                    mutableIndex.getOrPut(word) { mutableListOf() }.add(index)
                }
        }

        return HashMap(
            mutableIndex.mapValues { (_, indexes) -> indexes.toList() }
        )
    }

    private fun extractWords(line: String): List<String> {
        return WORD_REGEX.findAll(line)
            .map { matchResult -> normalizeWord(matchResult.value) }
            .filter { it.isNotBlank() }
            .toList()
    }

    private fun normalizeWord(word: String): String {
        return word
            .lowercase(Locale.ROOT)
            .replace(NON_LETTER_OR_DIGIT_REGEX, "")
    }

    private fun buildQuote(index: Int): String? {
        val line: String = lines[index]
        val prefix = getSpacePrefix(line)
        var start = index
        while (start >= 0 && getSpacePrefix(lines[start]) == prefix) {
            start--
        }
        var end = index
        while (end < lines.size && getSpacePrefix(lines[end]) == prefix) {
            end++
        }

        val builder = StringBuilder()
        for (i in start + 1 until end) {
            if (builder.length > 0) {
                builder.append("\n")
            }
            builder.append(lines[i].trim())
        }

        return builder.toString()
    }

    private fun getSpacePrefix(line: String): String {
        val builder = StringBuilder()
        for (c in line) {
            if (c == ' ') builder.append(" ") else break
        }
        return builder.toString()
    }

    private fun String.escapeForJson(): String {
        return this
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

    private companion object {
        val WORD_REGEX = Regex("[\\p{L}\\p{Nd}']+")
        val NON_LETTER_OR_DIGIT_REGEX = Regex("[^\\p{L}\\p{Nd}]+")
    }
}
