package com.github.mobdev778.aiadventchallenge.domain.agent.model

import java.util.UUID

/**
 * Запрос к агенту, представляющий событие или команду от пользователя.
 *
 * @param chatId Идентификатор чата, в рамках которого происходит взаимодействие.
 * @param taskContextId Идентификатор контекста задачи (опционально). Позволяет связать запрос с конкретной задачей.
 * @param parentMessageId Идентификатор родительского сообщения,
 *   если запрос является продолжением ветки диалога (опционально).
 * @param time Время отправки запроса в миллисекундах (Unix time).
 * @param query Текстовый запрос пользователя.
 * @param meta Произвольные метаданные, связанные с запросом (опционально).
 */
data class AgentRequest(
    val chatId: UUID,
    val taskContextId: UUID?,
    val parentMessageId: UUID?,
    val time: Long,
    val query: String,
    val meta: Any? = null,
)
