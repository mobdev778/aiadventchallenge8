package com.github.mobdev778.aiadventchallenge.domain.mcpserver

import io.modelcontextprotocol.kotlin.sdk.types.Tool
import org.koin.core.annotation.Single
import java.util.concurrent.ConcurrentHashMap

@Single
class CachedMcpToolsChecker(
    private val rawChecker: McpToolsChecker,
) {

    private val cache = ConcurrentHashMap<String, List<Tool>>()

    suspend fun loadTools(url: String): List<Tool> {
        return cache.getOrPut(url) {
            rawChecker.loadTools(url)
        }
    }
}
