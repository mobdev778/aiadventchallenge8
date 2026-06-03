package com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Стратегия "Группа экспертов (Panel of Experts)"
 */
class PanelOfExpertsStrategy(
    private val directAnswerStrategy: DirectAnswerStrategy,
) : ReasoningStrategy {

    override val name: String = "Группа экспертов (Panel of Experts)"

    override suspend fun solve(
        system: String?,
        user: String
    ): String {
        val expertPrompts = listOf(
            "Ты — эксперт-аналитик. Твоя специализация — декомпозиция " +
                    "сложных задач и анализ всех возможных подходов. " +
                    "Реши задачу и обоснуй каждый шаг:\n\n[UserRequest]",

            "Ты — инженер-практик. Твоя специализация — поиск эффективных " +
                    "и реализуемых решений. Предложи конкретное решение:\n\n[UserRequest]",

            "Ты — критик. Твоя специализация — поиск ошибок и слабых мест. " +
                    "Укажи на типичные ошибки в подобных задачах и предложи " +
                    "наиболее надёжный подход:\n\n[UserRequest]"
        )

        val responses = coroutineScope {
            expertPrompts.map { expertPrompt ->
                async {
                    directAnswerStrategy.solve(expertPrompt, user)
                }
            }.awaitAll()
        }

        val aggregatedPrompt = buildString {
            appendLine("Исходная задача: $user\n")
            responses.forEach { response ->
                if (response != ReasoningStrategy.NO_ANSWER) {
                    appendLine("### Мнение эксперта:")
                    appendLine(response)
                    appendLine()
                }
            }
            appendLine(
                "Проанализируй мнения всех экспертов, найди точки согласия " +
                        "и разногласия, и сформулируй итоговое решение задачи."
            )
        }

        return directAnswerStrategy.solve(system, aggregatedPrompt)
    }
}