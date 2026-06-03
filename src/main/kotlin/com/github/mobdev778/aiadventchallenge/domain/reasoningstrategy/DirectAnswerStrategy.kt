package com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy

import com.embeddings.rag.BuildConfig
import com.github.mobdev778.aiadventchallenge.domain.openai.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.openai.Message
import com.github.mobdev778.aiadventchallenge.domain.openai.OpenAIClient
import com.github.mobdev778.aiadventchallenge.domain.openai.Role

/**
 * Стратегия "Прямой ответ" (Direct Answer).
 *
 * Передаем задачу LLM-ке "как есть", без каких-либо дополнений и подсказок.
 */
class DirectAnswerStrategy(
    private val client: OpenAIClient,
) : ReasoningStrategy {

    override val name: String = "Прямой ответ (Direct Answer)"

    override suspend fun solve(
        system: String?,
        user: String,
    ): String {
        val messages = mutableListOf<Message>()
        system?.let {
            messages.add(
                Message(role = Role.System, content = it)
            )
        }

        messages.add(
            Message(role = Role.User, content = user)
        )

        val response = client.execute(
            ChatRequest(
                model = BuildConfig.MODEL,
                messages = messages,
            )
        )
        return response.choices.firstOrNull()?.message?.content ?: ReasoningStrategy.NO_ANSWER
    }
}