package com.github.mobdev778.aiadventchallenge.domain.contextmanagement.stickyFacts

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.chat.model.MessageType
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class GetStickyFactsUseCase(
    private val settingsRepository: SettingsRepository,
    private val chatClient: ChatClient,
) {

    /**
     * Получает "Sticky Facts" для заданной группы сообщений
     *
     * @param messages сообщения, для которых нужно вычислить "Sticky Facts"
     *
     * @return "саммари".
     */
    suspend fun invoke(messages: List<ChatMessage>): ChatMessage? {
        val chatText = buildChatText(messages)
        val prompt = buildStickyFactsPrompt(chatText)
        val baseModel = settingsRepository.getSettings().baseModel.trim().replace("\n", "")
        return executeStickyFactsRequest(messages, baseModel, prompt)
    }

    private fun buildChatText(messages: List<ChatMessage>): String {
        return messages.mapNotNull {
            when (it.type) {
                MessageType.User -> "Пользователь: ${it.text}"
                MessageType.Bot -> "Ассистент: ${it.text}"
                else -> null
            }
        }.joinToString("\n")
    }

    private fun buildStickyFactsPrompt(chatText: String): String {
        return "Ты — модуль управления долговременной памятью (Sticky Facts) для LLM-ассистента.\n" +
                "Твоя задача — извлечь из диалога ТОЛЬКО критически важные факты, " +
                "которые необходимы для продолжения работы, когда старые сообщения удалятся из контекста.\n" +
                "\n" +
                "КРИТИЧЕСКИЕ ПРАВИЛА:\n" +
                "1. ИГНОРИРУЙ приветствия, вежливость, пустой флуд и мета-комментарии " +
                "(например: 'Привет', 'Чем помочь', 'Понял', 'Секунду').\n" +
                "2. Сохраняй только жесткие данные в формате пар Ключ=Значение:\n" +
                "   - Профиль пользователя (имя, стек технологий, часовой пояс, ограничения).\n" +
                "   - Текущая задача (что именно делает пользователь, цель, требования).\n" +
                "   - Технические детали (версии ПО, коды ошибок, ссылки, структуры данных, куски кода).\n" +
                "   - Финальные решения (на каком варианте остановились, что было исправлено).\n" +
                "3. Если в тексте НЕТ новых критически важных фактов, задач или технических деталей, " +
                "НЕ ВЫВОДИ ВООБЩЕ НИЧЕГО. Твой ответ должен быть абсолютно пустым. " +
                "Никаких фраз вроде 'Нет фактов' или 'Sticky_Facts: нет'. Строго 0 символов.\n" +
                "4. Формат вывода: Строго пары КЛЮЧ:ЗНАЧЕНИЕ, разделенные переносом строки. " +
                "Без кавычек вокруг ключей, если это не нужно. Никакого текста от себя.\n" +
                "\n" +
                "Пример 1 (Есть факты):\n" +
                "Вход: 'Привет! Я Руслан, пишу проект на Kotlin. У меня падает OutOfMemory.'\n" +
                "Результат: \n" +
                "Имя_пользователя: Руслан\n" +
                "Стек_технологий: Kotlin\n" +
                "Текущая_проблема: Ошибка OutOfMemory\n" +
                "\n" +
                "Пример 2 (Нет фактов):\n" +
                "Вход: 'Привет! Чем могу помочь? — Да просто тестирую бота, привет! — Понял, обращайся.'\n" +
                "Результат:\n" +
                "\n" +
                "Входные сообщения для анализа:\n" +
                "${chatText}\n" +
                "\n" +
                "Результат сжатия (Sticky Facts):"
    }

    private suspend fun executeStickyFactsRequest(
        messages: List<ChatMessage>,
        baseModel: String,
        prompt: String,
    ): ChatMessage? {
        var result: ChatMessage? = null
        runCatching {
            chatClient.execute(
                ChatRequest(
                    model = baseModel,
                    messages = listOf(
                        Message(role = Role.User, content = prompt)
                    ),
                ),
            )
        }.onSuccess { response ->
            val responseTokens = response.usage?.completionTokens ?: 0
            val answer = response.choices.firstOrNull()?.message?.content
                ?.takeIf { it.isNotBlank() }

            answer?.let {
                result = ChatMessage(
                    id = UUID.randomUUID(),
                    chatId = messages.first().chatId,
                    parentId = null,
                    branchB = false,
                    time = messages.first().time,
                    text = answer,
                    type = MessageType.Bot,
                    tokens = responseTokens,
                    rank = 0,
                )
            }
        }

        return result
    }
}
