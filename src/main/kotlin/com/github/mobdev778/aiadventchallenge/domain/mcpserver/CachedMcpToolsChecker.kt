package com.github.mobdev778.aiadventchallenge.domain.mcpserver

import io.modelcontextprotocol.kotlin.sdk.types.Tool
import org.koin.core.annotation.Single
import java.util.concurrent.ConcurrentHashMap

/**
 * Кэширующая обёртка над [McpToolsChecker], сохраняющая результаты загрузки списка инструментов
 * для каждого уникального URL в потокобезопасном кэше [ConcurrentHashMap].
 *
 * Используется для повторного использования ранее полученных списков инструментов без
 * повторных HTTP-запросов к MCP-серверу.
 *
 * @property rawChecker низкоуровневый компонент для загрузки инструментов с сервера.
 */
@Single
class CachedMcpToolsChecker(
    private val rawChecker: McpToolsChecker,
) {

    /**
     * Потокобезопасный кэш, отображающий URL на соответствующий список инструментов.
     */
    private val cache = ConcurrentHashMap<String, List<Tool>>()

    /**
     * Загружает список инструментов для указанного URL. При повторных вызовах с тем же URL
     * возвращает кэшированный результат без обращения к серверу.
     *
     * @param url базовый адрес MCP-сервера (нормализуется внутри [McpToolsChecker.loadTools]).
     * @return список инструментов [Tool], доступных на сервере.
     */
    suspend fun loadTools(url: String): List<Tool> {
        return cache.getOrPut(url) {
            rawChecker.loadTools(url)
        }
    }
}
