package com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model

import kotlinx.serialization.Serializable

/**
 * DTO-объект ответа планирования, возвращаемый chat-клиентом.
 *
 * Представляет результат анализа запроса: является ли он задачей, содержит ли
 * название задачи и пошаговый план действий. Используется на уровне данных
 * для десериализации ответов от серверной части или AI-модели.
 *
 * @property isTask Флаг, указывающий, что запрос распознан как задача, требующая выполнения.
 * @property taskName Название задачи. Может быть `null`, если запрос не является задачей
 *                    или название не определено.
 * @property plan Упорядоченный список шагов плана выполнения задачи. Может быть `null`,
 *                если задача не требует плана или запрос не является задачей.
 */
@Serializable
data class PlanningResponseDto(
    val isTask: Boolean,
    val taskName: String? = null,
    val plan: List<String>? = null
)
