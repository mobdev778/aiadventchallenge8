package com.github.mobdev778.aiadventchallenge.domain.chatclient.model

import io.modelcontextprotocol.kotlin.sdk.types.Tool

/**
 * Запрос к языковой модели, инкапсулирующий все параметры вызова.
 *
 * Этот data-класс используется для формирования запроса в чат-клиенте, агрегируя
 * идентификатор модели, уровень усилий рассуждения (см. [ReasoningEffort]),
 * историю сообщений ([Message]), температуру генерации и список доступных инструментов.
 *
 * @param model Идентификатор модели, которую необходимо использовать.
 * @param reasoningEffort Уровень усилий, прилагаемых моделью для выполнения рассуждений.
 *                        Может быть `null` – тогда используется значение по умолчанию.
 * @param messages История диалога в виде списка сообщений [Message].
 * @param temperature Температура генерации (степень случайности ответов). По умолчанию `0.7`.
 * @param tools Список инструментов ([Tool]), доступных модели. По умолчанию пустой.
 */
data class ChatRequest(
    val model: String,
    val reasoningEffort: ReasoningEffort? = null,
    val messages: List<Message>,
    val temperature: Double = 0.7,
    val tools: List<Tool> = emptyList(),
)
