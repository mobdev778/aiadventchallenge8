package com.github.mobdev778.aiadventchallenge.data.openai.datasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequestDto(
    /** Название модели, которую нужно использовать для генерации (например, "gpt-4o"). */
    val model: String,

    /** Список сообщений, составляющих историю диалога. */
    val messages: List<MessageDto>,

    /** Температура генерации (от 0.0 до 2.0). Выше значение — креативнее ответ, ниже — точнее и предсказуемее. */
    val temperature: Double,

    /** Максимальное количество токенов, которое модель может сгенерировать в ответе. */
    @SerialName("max_tokens")
    val maxTokens: Int? = null,

    /** Список стоп-последовательностей (до 4 строк). При встрече любой из них модель прекратит генерацию. */
    val stop: List<String>? = null,
)