package com.github.mobdev778.aiadventchallenge.domain.agent.model

import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.invariant.Invariant
import com.github.mobdev778.aiadventchallenge.domain.profile.model.Profile
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import io.modelcontextprotocol.kotlin.sdk.types.Tool

/**
 * Контекст, передаваемый агенту для выполнения шага решения задачи.
 *
 * Объединяет всю информацию, необходимую агенту для генерации или обработки ответа:
 * активный профиль пользователя, состояние текущей задачи, историю сообщений окна,
 * список применимых инвариантов (правил валидации) и доступные инструменты.
 *
 * @property profile Активный профиль пользователя, определяющий контекст и стиль поведения.
 * @property taskContext Контекст текущей задачи
 *   (план, выполненные шаги, текущий этап), либо null, если задача не активна.
 * @property windowMessages Сообщения в текущем окне диалога, используемые как история.
 * @property invariants Список инвариантов, которые должны быть проверены для ответа агента.
 * @property tools Список инструментов, доступных агенту для выполнения действий.
 */
data class AgentContext(
    val profile: Profile,
    val taskContext: TaskContext?,
    val windowMessages: List<ChatMessage>,
    val invariants: List<Invariant>,
    val tools: List<Tool>,
)
