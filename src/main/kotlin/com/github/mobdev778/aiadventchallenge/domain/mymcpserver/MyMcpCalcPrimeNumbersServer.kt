package com.github.mobdev778.aiadventchallenge.domain.mymcpserver

import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.util.Collections
import kotlin.math.sqrt

class MyMcpCalcPrimeNumbersServer(
    private val scope: CoroutineScope,
) : BaseMyMcpServer(
    name = "MyMcpCalcPrimeNumbersServer",
    description = "Локальный MCP-сервер непрерывного вычисления простых чисел",
    port = 3001,
    launchAtStartup = false,
) {

    private val knownPrimeNumbers = Collections.synchronizedSet(hashSetOf<Int>())
    @Volatile
    private var maxKnownPrimeNumber: Int? = null
    @Volatile
    private var nextCandidate: Int = 2

    private var calculationJob: Job? = null

    override fun createServer(): Server {
        val server = Server(
            serverInfo = Implementation(
                name = "my-mcp-prime-numbers-server",
                version = "1.0.0",
            ),
            options = ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = false),
                ),
            ),
            instructions = "Local MCP server for continuous prime number calculation",
        )

        server.addTool(
            name = "get_max_known_prime_number",
            description = "Returns the maximum prime number currently known to the server",
            inputSchema = ToolSchema(
                properties = buildJsonObject {},
                required = emptyList(),
            ),
        ) { _: CallToolRequest ->
            textResult(maxKnownPrimeNumber?.toString() ?: "null")
        }

        server.addTool(
            name = "check_is_known_prime_number",
            description = "Checks whether the provided Int number is already known by the server to be prime",
            inputSchema = ToolSchema(
                properties = buildJsonObject {
                    put("number", buildJsonObject {
                        put("type", JsonPrimitive("integer"))
                        put("description", JsonPrimitive("Int number to check"))
                    })
                },
                required = listOf("number"),
            ),
        ) { request: CallToolRequest ->
            val number = request.params.arguments?.get("number")?.jsonPrimitive?.intOrNull
                ?: return@addTool textResult("null")
            textResult(checkIsKnownPrimeNumber(number))
        }

        return server
    }

    override suspend fun start() {
        if (calculationJob?.isActive == true) return
        super.start()
        startPrimeCalculation()
    }

    override suspend fun stop() {
        calculationJob?.cancel()
        calculationJob = null
        super.stop()
    }

    private fun startPrimeCalculation() {
        calculationJob = scope.launch {
            while (isActive && nextCandidate >= 2) {
                val candidate = nextCandidate
                if (isPrime(candidate)) {
                    knownPrimeNumbers.add(candidate)
                    maxKnownPrimeNumber = candidate
                }
                if (candidate == Int.MAX_VALUE) {
                    break
                }
                nextCandidate = candidate + 1
                yield()
            }
        }
    }

    private fun checkIsKnownPrimeNumber(number: Int): String {
        val currentMaxPrime = maxKnownPrimeNumber ?: return "null"
        if (number > currentMaxPrime) return "null"
        return if (knownPrimeNumbers.contains(number)) "true" else "false"
    }

    private fun isPrime(number: Int): Boolean {
        if (number < 2) return false
        if (number == 2) return true
        if (number % 2 == 0) return false

        val limit = sqrt(number.toDouble()).toInt()
        var divisor = 3
        while (divisor <= limit) {
            if (number % divisor == 0) return false
            divisor += 2
        }
        return true
    }

    private fun textResult(text: String): CallToolResult =
        CallToolResult(
            content = listOf(TextContent(text = text)),
            isError = false,
        )
}
