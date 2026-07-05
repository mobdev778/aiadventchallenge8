package com.github.mobdev778.aiadventchallenge.domain.mymcpserver.rag

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import org.koin.core.annotation.Single

@Single
class RagRussianFilter(
    private val settingsRepository: SettingsRepository,
    private val chatClient: ChatClient,
) {

    suspend fun filter(query: String): String {
        return if (hasRussianLetters(query)) {
            val settings = settingsRepository.getSettings()
            val response = chatClient.execute(
                request = ChatRequest(
                    model = settings.baseModel,
                    messages = listOf(
                        Message(
                            role = Role.System,
                            content = "Translate user prompt into English",
                        ),
                        Message(
                            role = Role.User,
                            content = query
                        )
                    )
                )
            )
            response.choices.firstOrNull()?.message?.content ?: query
        } else {
            query
        }
    }

    private val RUSSIAN_LETTERS = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя".toSet()

    private fun hasRussianLetters(query: String): Boolean {
        for (c in query) {
            if (RUSSIAN_LETTERS.contains(c)) {
                return true
            }
        }
        return false
    }
}
