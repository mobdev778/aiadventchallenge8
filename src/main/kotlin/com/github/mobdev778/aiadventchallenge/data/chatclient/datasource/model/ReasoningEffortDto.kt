package com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO-перечисление, представляющее уровень усилий, затрачиваемых моделью на
 * рассуждение (reasoning effort). Используется в запросах к API чат-клиента
 * для управления глубиной логического анализа ответа.
 *
 * Сериализуется в JSON с помощью [SerialName] в строковые значения:
 * `"low"`, `"medium"` и `"high"`.
 */
@Serializable
enum class ReasoningEffortDto {
    /**
     * Низкий уровень усилий — модель генерирует ответ с минимальными
     * рассуждениями, предпочитая скорость глубине анализа.
     */
    @SerialName("low")
    Low,

    /**
     * Средний уровень усилий — сбалансированный подход между скоростью
     * генерации и качеством логических выводов.
     */
    @SerialName("medium")
    Medium,

    /**
     * Высокий уровень усилий — модель выполняет углублённый анализ и
     * детальные рассуждения перед формированием ответа.
     */
    @SerialName("high")
    High
}
