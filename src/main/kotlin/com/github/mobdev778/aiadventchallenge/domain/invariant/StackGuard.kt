package com.github.mobdev778.aiadventchallenge.domain.invariant

data class StackGuard(
    override val id: String = "STACK_LIMIT",
    val allowedStack: Set<String>,
    val forbiddenKeywords: Set<String> // Явно задаем то, чего быть не должно
) : Invariant() {

    override val llmDescription: String = "Разрешенный стек: $allowedStack. Запрещено использовать: $forbiddenKeywords"

    override fun validate(request: String, response: String): ValidationResult {
        // 1. Если сам пользователь ничего не нарушал, проверять нечего
        // Но если в ответе всплыли запрещенные технологии:
        val foundForbidden = forbiddenKeywords.filter { it.lowercase() in response.lowercase() }

        if (foundForbidden.isNotEmpty()) {
            // Проверяем, не является ли это отказом (например, бот сам говорит "нельзя использовать X")
            val isRefusal = response.contains("не могу", ignoreCase = true) ||
                    response.contains("запрещено", ignoreCase = true)

            if (!isRefusal) {
                return ValidationResult.Failed(
                    "Бот попытался предложить запрещенные технологии: $foundForbidden"
                )
            }
        }
        return ValidationResult.Passed
    }
}