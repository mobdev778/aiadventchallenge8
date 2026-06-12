package com.github.mobdev778.aiadventchallenge.domain.messageselection

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatAuthor
import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatMessage
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class GetSummaryUseCase(
    private val settingsRepository: SettingsRepository,
    private val chatClient: ChatClient,
) {

    /**
     * Получает "summary" для заданной группы сообщений
     *
     * @param messages сообщения, для которых нужно вычислить "summary"
     *
     * @return "саммари".
     */
    suspend fun invoke(messages: List<ChatMessage>): ChatMessage? {
        val chatText = messages.joinToString("\n") {
            if (it.author == ChatAuthor.User) {
                "Пользователь: ${it.text}"
            } else {
                "Ассистент: ${it.text}"
            }
        }

        val prompt =
            "Ты — модуль сжатия контекста диалога. Твоя задача — превратить чат в одну компактную, но информационно плотную выжимку (summary).\n" +
                    "\n" +
                    "        КРИТИЧЕСКИЕ ПРАВИЛА:\n" +
                    "        1. Сохраняй все конкретные факты: имена, даты, числа, названия технологий, ссылки, коды ошибок и цитаты.\n" +
                    "        2. Четко фиксируй суть: кто (пользователь или ассистент), какое действие совершил, какую проблему решил или какой вопрос задал.\n" +
                    "        3. Опускай весь \"шум\": приветствия, вежливость, слова-паразиты и вводные фразы (например, вместо \"Пользователь вежливо поздоровался и спросил...\" пиши \"Запрос пользователя:...\").\n" +
                    "        4. Формат вывода должен быть максимально сжатым (список тезисов или плотный текст). Никаких вводных слов от себя (\"Вот ваше краткое содержание:\"). Только сам результат.\n" +
        "\n" +
                "        Входные сообщения для сжатия:\n" +
                "        ${chatText}\n" +
                "\n" +
                "        Результат сжатия:"

        val baseModel = settingsRepository.getSettings().baseModel
            .trim()
            .replace("\n", "")

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
                    parentId = null,
                    time = messages.first().time,
                    text = answer,
                    author = ChatAuthor.Bot,
                    tokens = responseTokens,
                    rank = 0,
                )
            }
        }
        return result
    }
}