package com.github.mobdev778.aiadventchallenge.domain.agent.model

import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext

/**
 * Ответ агента на запрос пользователя ([AgentRequest]), который может быть как окончательным, так и промежуточным.
 *
 * Представляет собой результат обработки запроса, включая текстовое сообщение, ответы от использованных инструментов,
 * информацию о потреблённых токенах и обновлённый контекст задачи.
 *
 * @property intermediate Признак промежуточного ответа
 *   (true — ответ не является финальным, false — окончательный результат).
 * @property agent Имя или идентификатор агента, сгенерировавшего данный ответ.
 * @property request Исходный запрос ([AgentRequest]), на который формируется ответ.
 * @property message Текстовое сообщение, сгенерированное агентом в ответ на запрос.
 * @property toolMessages Список ответов от использованных инструментов ([ToolResponse]),
 *   полученных в ходе обработки запроса.
 * @property requestTokens Количество токенов, затраченных на обработку запроса (входные токены).
 * @property responseTokens Количество токенов, использованных для генерации ответа (выходные токены).
 * @property taskContext Обновлённый [TaskContext] для задачи, с которой связан запрос,
 *   или `null`, если контекст не обновлялся.
 */
data class AgentResponse(
    val intermediate: Boolean = false, // признак промежуточного ответа
    val agent: String,
    val request: AgentRequest,
    val message: String,
    val toolMessages: List<ToolResponse>,
    val requestTokens: Int,
    val responseTokens: Int,
    val taskContext: TaskContext?, // обновленный контекст задачи
)
