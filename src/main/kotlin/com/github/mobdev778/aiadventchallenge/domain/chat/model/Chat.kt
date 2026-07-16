package com.github.mobdev778.aiadventchallenge.domain.chat.model

import java.util.UUID

/**
 * Модель данных чата в доменном слое.
 *
 * Представляет собой сущность чата с уникальным идентификатором, названием,
 * временной меткой, а также возможными связями с родительским чатом и контекстом задачи.
 *
 * @property id Уникальный идентификатор чата.
 * @property name Отображаемое имя чата.
 * @property time Временная метка (например, время создания или обновления).
 * @property parentId Идентификатор родительского чата, если чат является дочерним; иначе null.
 * @property taskContextId Идентификатор контекста задачи, с которой связан чат; может быть null.
 */
data class Chat(
    val id: UUID,
    val name: String,
    val time: Long,
    val parentId: UUID?,
    val taskContextId: UUID?,
)
