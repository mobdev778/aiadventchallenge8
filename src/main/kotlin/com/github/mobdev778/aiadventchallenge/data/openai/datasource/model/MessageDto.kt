package com.github.mobdev778.aiadventchallenge.data.openai.datasource.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    /** Роль автора сообщения (например: "system", "user", "assistant" или "tool"). */
    val role: RoleDto,

    /** Текст сообщения. Может быть null, если модель вместо текста возвращает вызов функции (tool_calls). */
    val content: String?,
)