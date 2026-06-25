package com.github.mobdev778.aiadventchallenge.domain.mymcpserver

import com.github.mobdev778.aiadventchallenge.domain.mcpserver.McpToolsChecker
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MyMcpNettyServerIntegrationTest {

    @Test
    fun `loads tools from my mcp netty server`(): Unit = runBlocking {
        val server = MyMcpNettyServer()

        try {
            server.start(8300)
            val tools = McpToolsChecker().loadTools("http://localhost:8300/mcp").map { it.name }
            assertTrue(tools.contains("find_files_by_name"))
            assertTrue(tools.contains("read_project_file"))
            assertTrue(tools.contains("list_project_files"))
        } finally {
            server.stop()
        }
    }
}
