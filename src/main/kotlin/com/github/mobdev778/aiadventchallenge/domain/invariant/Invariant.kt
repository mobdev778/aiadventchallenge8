package com.github.mobdev778.aiadventchallenge.domain.invariant

abstract class Invariant {
    abstract val id: String
    abstract val llmDescription: String // описание инварианта для LLM

    // Передаем и запрос пользователя, и ответ бота для полной картины
    abstract fun validate(request: String, response: String): ValidationResult
}
