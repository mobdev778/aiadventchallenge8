package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.ReasoningEffortDto
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ReasoningEffort
import org.koin.core.annotation.Single

/**
 * Маппер, преобразующий доменную модель [ReasoningEffort] в DTO-модель [ReasoningEffortDto].
 *
 * Используется для конвертации уровня усилий рассуждения перед отправкой
 * в API чат-клиента. Компонент зарегистрирован как синглтон в Koin.
 */
@Single
class ReasoningEffortMapper {

    /**
     * Преобразует исходное значение уровня усилий в его DTO-представление.
     *
     * @param reasoningEffort Доменное перечисление, определяющее глубину рассуждения модели.
     * @return Соответствующее DTO-значение с готовым к сериализации именем.
     */
    fun map(reasoningEffort: ReasoningEffort): ReasoningEffortDto {
        return when (reasoningEffort) {
            ReasoningEffort.High -> ReasoningEffortDto.High
            ReasoningEffort.Medium -> ReasoningEffortDto.Medium
            ReasoningEffort.Low -> ReasoningEffortDto.Low
        }
    }
}
