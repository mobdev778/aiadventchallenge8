package com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy

import com.embeddings.rag.BuildConfig
import com.github.mobdev778.aiadventchallenge.domain.openai.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.openai.Message
import com.github.mobdev778.aiadventchallenge.domain.openai.OpenAIClient
import com.github.mobdev778.aiadventchallenge.domain.openai.Role

class StepByStepStrategy(
    private val client: OpenAIClient,
) : ReasoningStrategy {

    override val name: String = "Пошаговое решение (Chain of Thought)"

    override suspend fun solve(
        system: String?,
        user: String,
        temperature: Double,
    ): String {
        val messages = mutableListOf<Message>()

        messages.add(
            Message(
                role = Role.System,
                content = "**Инструкция:** Решай задачу строго пошагово, расписывая логику каждого действия.",
            )
        )

        system?.let {
            messages.add(
                Message(role = Role.System, content = system)
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