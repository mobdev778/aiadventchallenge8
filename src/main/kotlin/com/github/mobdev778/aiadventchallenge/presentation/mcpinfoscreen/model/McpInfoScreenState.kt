package com.github.mobdev778.aiadventchallenge.presentation.mcpinfoscreen.model

import androidx.compose.runtime.Immutable
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.model.McpServer

/**
 * Состояние экрана с информацией о MCP-сервере (Model Context Protocol).
 * Хранит все данные, необходимые для отображения экрана подробной информации.
 *
 * @property server текущий объект [McpServer], если загрузка завершена; `null` при отсутствии данных.
 * @property isLoading флаг, указывающий, выполняется ли загрузка данных в данный момент.
 * @property toolsText текстовое представление инструментов сервера (обычно отформатированный список).
 */
@Immutable
data class McpInfoScreenState(
    val server: McpServer? = null,
    val isLoading: Boolean = false,
    val toolsText: String = "",
)
