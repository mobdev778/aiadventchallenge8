package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model

import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage

/**
 * Модель UI-представления сообщения в чате.
 *
 * Содержит как оригинальное [ChatMessage], так и дополнительные поля,
 * необходимые для отображения иерархической структуры переписки
 * (ветвление сообщений, раскрытие/сворачивание дочерних элементов) и
 * отслеживания видимости сообщения в текущем видимом окне списка.
 *
 * @property message базовое доменное сообщение чата.
 * @property rank порядковый номер (ранг) сообщения в иерархии.
 * @property insideWindow флаг, показывающий, попадает ли сообщение в видимую область экрана.
 * @property children список дочерних сообщений (ответов) данного сообщения.
 * @property expanded флаг, указывающий, раскрыт ли список дочерних сообщений.
 * @property time временная метка сообщения (например, timestamp в миллисекундах).
 */
data class ChatUiMessage(
    val message: ChatMessage,
    val rank: Int,
    val insideWindow: Boolean,
    val children: List<ChatUiMessage>,
    val expanded: Boolean,
    val time: Long,
)
