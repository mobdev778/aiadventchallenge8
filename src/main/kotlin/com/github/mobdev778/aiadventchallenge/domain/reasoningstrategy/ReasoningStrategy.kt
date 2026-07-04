package com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy

/**
 * Интерфейс стратегии рассуждения
 */
interface ReasoningStrategy {

    /**
     * Название стратегии рассуждения
     */
    val name: String

    /**
     * Отправляет промпт LLM и возвращает ответ.
     *
     * @param system - системный промпт (необязательный параметр)
     * @param user - промпт пользователя.
     *
     * @return сгенерированный ответ или константа NO_ANSWER, если произошла ошибка
     */
    suspend fun solve(
        system: String?,
        user: String,
        temperature: Double = 0.7,
    ): String

    companion object {
        const val NO_ANSWER = "-== NO ANSWER ==-"
    }
}
