package com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy

import com.embeddings.rag.BuildConfig
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.Message
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.Role

/**
 * Стратегия "Прямой ответ" (Direct Answer).
 *
 * Передаем задачу LLM-ке "как есть", без каких-либо дополнений и подсказок.
 */
class DirectAnswerStrategy(
    private val client: ChatClient,
) : ReasoningStrategy {

    override val name: String = "Прямой ответ (Direct Answer)"

    override suspend fun solve(
        system: String?,
        user: String,
        temperature: Double,
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
                temperature = temperature,
            )
        )
        return response.choices.firstOrNull()?.message?.content ?: ReasoningStrategy.NO_ANSWER
    }
}