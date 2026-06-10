package com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy

import org.koin.core.annotation.Single

/**
 * Стратегия "Мета-промпт" (двухэтапная стратегия).
 *
 * Сначала просим LLM составить идеальный промпт для этой задачи,
 * а вторым запросом — решаем задачу по этому сгенерированному промпту.
 */
@Single
class MetaPromptStrategy(
    private val directAnswerStrategy: DirectAnswerStrategy,
) : ReasoningStrategy {

    override val name: String = "\"Мета-промпт\" (двухэтапная стратегия)"

    override suspend fun solve(
        system: String?,
        user: String,
        temperature: Double,
    ): String {
        val generatedPrompt = directAnswerStrategy.solve(
            system = "На основе следующей задачи создай идеальный, детальный промпт для LLM, " +
                    "который поможет решить её максимально точно. Верни только текст промпта. Задача: \n$[UserRequest]",
            user = "$system $user",
            temperature = temperature,
        )
        return when {
            generatedPrompt == ReasoningStrategy.NO_ANSWER -> directAnswerStrategy.solve(system, user)
            else -> directAnswerStrategy.solve(null, generatedPrompt)
        }
    }
}