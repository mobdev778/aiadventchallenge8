package com.github.mobdev778.aiadventchallenge.domain.contextmanagement

import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import java.util.UUID

/**
 * Стратегия управления контекстом.
 */
interface ContextManagementStrategy {

    /**
     * Выбирает из полной истории сообщений чата те, которые необходимо показать в данный момент.
     */
    suspend fun selectMessages(
        chatId: UUID,
        history: List<ChatMessage>,
    ): List<ChatMessage>


    /**
     * Удаляет данные для текущего чата.
     */
    suspend fun clear(chatId: UUID)
}
