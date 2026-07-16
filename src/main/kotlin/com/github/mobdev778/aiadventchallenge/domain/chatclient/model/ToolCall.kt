package com.github.mobdev778.aiadventchallenge.domain.chatclient.model

/**
 * Представляет вызов инструмента (tool call) в ответе модели.
 *
 * Этот класс инкапсулирует информацию, необходимую для выполнения вызова внешней функции
 * (инструмента), определённой клиентом. Обычно используется при интеграции с языковыми
 * моделями, поддерживающими механизм tool calling (например, OpenAI).
 *
 * @param id Уникальный идентификатор вызова инструмента.
 * @param type Тип вызова; в OpenAI всегда равен `"function"`.
 * @param function Детали вызова функции, включая имя и аргументы.
 */
data class ToolCall(
    val id: String,
    // В OpenAI всегда "function"
    val type: String,
    val function: FunctionCall,
)
