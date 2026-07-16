package com.github.mobdev778.aiadventchallenge.domain.invariant

/**
 * Инвариант проверки технологического стека — следит за тем, чтобы в ответе бота
 * не упоминались запрещённые ключевые слова без явного отказа.
 *
 * Позволяет задать разрешённый стек для контекста LLM ([allowedStack]) и
 * конкретный список ключевых слов, присутствие которых в ответе недопустимо ([forbiddenKeywords]).
 * Инвариант считается пройденным, если ответ не содержит запрещённых терминов или
 * если содержащиеся термины являются частью шаблонного отказа.
 *
 * @property allowedStack множество описаний разрешённых технологий, передаваемых в LLM.
 * @property forbiddenKeywords ключевые слова, появление которых в ответе бота контролируется.
 */
data class StackGuard(
    override val id: String = "STACK_LIMIT",

    /**
     * Набор разрешённых технологических стеков. Используется для формирования
     * описания инварианта для языковой модели.
     */
    val allowedStack: Set<String>,

    /**
     * Список запрещённых ключевых слов. Если они появляются в ответе бота и не
     * являются частью явного отказа, инвариант считается нарушенным.
     */
    val forbiddenKeywords: Set<String> // Явно задаем то, чего быть не должно
) : Invariant() {

    override val llmDescription: String = "Разрешенный стек: $allowedStack. Запрещено использовать: $forbiddenKeywords"

    /**
     * Проверяет соблюдение инварианта: наличие запрещённых ключевых слов в ответе.
     *
     * @param request текст запроса пользователя (в данной реализации не используется).
     * @param response текст ответа бота.
     * @return [ValidationResult.Passed], если запрещённые ключевые слова отсутствуют
     * или являются частью отказа; иначе [ValidationResult.Failed] с указанием
     * найденных запрещённых терминов.
     */
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
