package com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import org.koin.core.annotation.Single

/**
 * Стратегия "Прямой ответ" (Direct Answer).
 *
 * Передаем задачу LLM-ке "как есть", без каких-либо дополнений и подсказок.
 */
@Single
class DirectAnswerStrategy(
    private val settingsRepository: SettingsRepository,
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
                model = settingsRepository.getSettings().baseModel,
                messages = messages,
                temperature = temperature,
            )
        )
        return response.choices.firstOrNull()?.message?.content ?: ReasoningStrategy.NO_ANSWER
    }
}