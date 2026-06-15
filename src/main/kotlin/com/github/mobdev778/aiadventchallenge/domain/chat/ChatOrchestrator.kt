package com.github.mobdev778.aiadventchallenge.domain.chat

import com.github.mobdev778.aiadventchallenge.data.chat.repository.ChatRepository
import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.data.taskcontext.repository.TaskContextRepository
import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.chat.model.MessageType
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import com.github.mobdev778.aiadventchallenge.domain.task.TaskState
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class ChatOrchestrator(
    private val settingsRepository: SettingsRepository,
    private val chatClient: ChatClient,
    private val taskStateMachine: TaskStateMachine,
    private val taskContextRepository: TaskContextRepository,
    private val chatRepository: ChatRepository,
) {

    suspend fun sendMessage(context: ChatContext, message: ChatMessage): Pair<ChatMessage, Int> {
        // 1. ПРОВЕРКА РАБОЧЕЙ ПАМЯТИ: Есть ли активная задача?
        if (context.taskContext == null || context.taskContext.state == TaskState.Done) {
            // Задачи нет — проверяем, не хочет ли пользователь создать её
            val taskContext = taskStateMachine.createContext(message.text)
            if (taskContext != null) {
                taskContextRepository.saveTaskContext(taskContext)
                val chat = chatRepository.observeChat(message.chatId).first()!!
                chatRepository.createChat(chat.copy(taskContextId = taskContext.id))
            }
        }

        // 2. СБОРКА СИСТЕМНОГО ПРОМПТА ИЗ ТРЕХ СЛОЕВ ПАМЯТИ
        val systemPrompt = if (context.taskContext != null) {
            // Если есть рабочая память, собираем динамический контекст
            SystemPromptBuilder()
                .profile(context.profile)           // Долговременная
                .context(context.taskContext) // Рабочая
                .query(message.text)
                .build()
        } else {
            // Если рабочей памяти нет, используем стандартный профиль
            "${context.profile.content}\nПользователь просто общается, помогай в свободном режиме."
        }

        // 3. Запрос к модели
        val (response,tokens) = sendMessage(
            context = context,
            systemPrompt = systemPrompt,
            message = message,
        )

        // 4. ОБНОВЛЕНИЕ РАБОЧЕЙ ПАМЯТИ
        // Передаем ответ в стейт-машину. Если там есть "next_step", рабочая память обновится
        context.taskContext?.let { currentContext ->
            val updatedContext = taskStateMachine.execute(currentContext, response.text)
            taskContextRepository.saveTaskContext(updatedContext)
            val chat = chatRepository.observeChat(message.chatId).first()!!
            chatRepository.createChat(chat.copy(taskContextId = context.taskContext.id))

            // Если стейт-машина перевела задачу в Done, можно вывести системное сообщение
            if (updatedContext.state == TaskState.Done) {
                // Опционально: сохранить успешное решение в долговременную память (базу знаний профиля)
            }
        }

        // 5. Возвращаем ответ для обновления краткосрочной памяти (чата)
        return response to tokens
    }

    private suspend fun sendMessage(
        context: ChatContext,
        systemPrompt: String,
        message: ChatMessage
    ): Pair<ChatMessage, Int> {
        val requestMessages = (context.messages + message)
            .map { message ->
                when (message.type) {
                    MessageType.User -> {
                        Message(
                            role = Role.User,
                            content = message.text
                        )
                    }

                    MessageType.Bot -> {
                        Message(
                            role = Role.Assistant,
                            content = message.text
                        )
                    }

                    MessageType.StickyFacts -> Message(
                        role = Role.System,
                        content = "[CRITICAL_STICKY_FACTS]\nИспользуй следующие неизменяемые пары ключ-значение для контекста. " +
                                "Ты обязан строго следовать этим данным и не имеешь права их выдумывать или менять:" +
                                message.text
                    )
                }
            }

        val baseModel = settingsRepository.getSettings().baseModel
            .trim()
            .replace("\n", "")

        val result = try {
            val response = chatClient.execute(
                ChatRequest(
                    model = baseModel,
                    messages = listOf(Message(Role.System, systemPrompt)) + requestMessages,
                ),
            )
            val promptTokens = response.usage?.promptTokens ?: 0

            val responseTokens = response.usage?.completionTokens ?: 0
            val answer = response.choices.firstOrNull()?.message?.content
                ?.takeIf { it.isNotBlank() }
                ?: "- no response -"

            ChatMessage(
                id = UUID.randomUUID(),
                chatId = message.chatId,
                parentId = message.id,
                branchB = false,
                time = System.currentTimeMillis(),
                text = answer,
                type = MessageType.Bot,
                tokens = responseTokens,
                rank = 0,
            ) to promptTokens
        } catch(e: Exception) {
            ChatMessage(
                id = UUID.randomUUID(),
                chatId = message.chatId,
                parentId = message.id,
                branchB = false,
                time = System.currentTimeMillis(),
                text = "Ошибка: ${e.message ?: e::class.java.simpleName}",
                type = MessageType.Bot,
                tokens = 0,
                rank = 0,
            ) to 0
        }
        return result
    }
}