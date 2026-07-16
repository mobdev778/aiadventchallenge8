package com.github.mobdev778.aiadventchallenge.domain.chatclient.model

/**
 * Модель, представляющая информацию об использовании токенов при взаимодействии с API модели.
 *
 * @property promptTokens Количество токенов, использованных во входном запросе (prompt).
 * @property completionTokens Количество токенов, сгенерированных моделью в ответе (completion).
 * @property totalTokens Общее количество использованных токенов (сумма prompt и completion).
 */
data class Usage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)
