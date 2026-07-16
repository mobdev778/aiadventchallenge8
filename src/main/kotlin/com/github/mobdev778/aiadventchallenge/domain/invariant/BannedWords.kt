package com.github.mobdev778.aiadventchallenge.domain.invariant

/**
 * Инвариант, проверяющий отсутствие запрещённых слов в ответе ассистента.
 *
 * Использует список запрещённых терминов [bannedTerms] и выполняет поиск
 * в тексте ответа с учётом границ слов (через `\b`), игнорируя регистр.
 * При обнаружении любого из терминов инвариант считается нарушенным.
 *
 * @property id уникальный идентификатор инварианта, по умолчанию `"BANNED_WORDS"`.
 * @property bannedTerms множество запрещённых слов (строк), которые не должны
 *   появляться в ответе.
 */
data class BannedWords(
    override val id: String = "BANNED_WORDS",
    val bannedTerms: Set<String>
) : Invariant() {

    /**
     * Описание инварианта, понятное языковой модели: запрет на использование
     * нецензурных и запрещённых слов.
     */
    override val llmDescription: String = "Не используй запрещенные (нецензурные) слова"

    /**
     * Выполняет проверку ответа на наличие запрещённых слов из [bannedTerms].
     *
     * @param request текст запроса пользователя (не используется в данной проверке).
     * @param response текст ответа ассистента, который проверяется на вхождения запрещённых терминов.
     * @return [ValidationResult.Passed], если ни одного запрещённого термина не найдено;
     *   [ValidationResult.Failed] с перечнем обнаруженных нарушений в противном случае.
     */
    override fun validate(request: String, response: String): ValidationResult {
        // Ищем, какие из запрещенных слов попали в ответ бота
        val foundViolations = bannedTerms.filter { term ->
            // Используем регулярное выражение с границами слов (\b),
            // чтобы "RxJava" триггерилось, а условное "RxJavaWrapper" — нет.
            val regex = Regex("\\b${Regex.escape(term)}\\b", RegexOption.IGNORE_CASE)
            regex.containsMatchIn(response)
        }

        return if (foundViolations.isNotEmpty()) {
            ValidationResult.Failed(
                "В ответе ассистента обнаружены запрещенные термины: $foundViolations"
            )
        } else {
            ValidationResult.Passed
        }
    }
}
