package com.github.mobdev778.aiadventchallenge.domain.invariant

data class BannedWords(
    override val id: String = "BANNED_WORDS",
    val bannedTerms: Set<String>
) : Invariant() {

    override val llmDescription: String = "Не используй запрещенные (нецензурные) слова"

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
