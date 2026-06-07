package com.github.mobdev778.aiadventchallenge.data.openai.chat.datasource.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatResponseDto(
    /** Уникальный идентификатор данной генерации (ответа чата). */
    val id: String,

    /** Название модели, которая использовалась для генерации ответа. */
    val model: String,

    /** Список вариантов ответов, сгенерированных моделью. */
    val choices: List<ChoiceDto>
)