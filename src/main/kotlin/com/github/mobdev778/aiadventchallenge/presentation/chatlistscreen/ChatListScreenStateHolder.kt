package com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagChatRepository
import com.github.mobdev778.aiadventchallenge.domain.chat.model.Chat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.UUID

private const val STATE_FLOW_TIMEOUT_MS = 5000L

/**
 * Хранитель состояния экрана списка чатов.
 *
 * Отвечает за предоставление списка чатов и обработку пользовательских событий,
 * преобразуя их в команды для навигации или в действия по изменению данных.
 *
 * Получает чаты из [RagChatRepository] в виде реактивного потока [chats] и
 * предоставляет поток команд [commands] для однократной обработки в UI-слое.
 * События, поступающие через [onEvent], преобразуются в соответствующие операции
 * с репозиторием или эмиссию команд.
 *
 * @property chats Поток состояния списка всех чатов, обновляемый из репозитория.
 * @property commands Поток команд для однократного выполнения, например, навигации.
 * @param ragChatRepository Репозиторий для доступа к данным чатов.
 * @param scope CoroutineScope, используемый для запуска асинхронных операций.
 */
@Single
class ChatListScreenStateHolder(
    private val ragChatRepository: RagChatRepository,
    private val scope: CoroutineScope,
) {
    /**
     * Поток состояния списка всех чатов, полученный из репозитория.
     * Обновляется автоматически при изменении данных в [RagChatRepository].
     */
    val chats: StateFlow<List<Chat>> = ragChatRepository
        .observeChats()
        .flowOn(Dispatchers.IO)
        .stateIn(scope, SharingStarted.WhileSubscribed(STATE_FLOW_TIMEOUT_MS), emptyList())

    /**
     * Общий поток команд, эмитирующий однократные навигационные или иные действия.
     * Используется с буфером в одну команду, чтобы гарантировать доставку до подписчика.
     */
    val commands = MutableSharedFlow<ChatListScreenCommand>(
        extraBufferCapacity = 1
    )

    /**
     * Обрабатывает событие от пользовательского интерфейса.
     *
     * В зависимости от типа события выполняет создание чата, удаление, эмиссию команд
     * для открытия чата или настроек.
     *
     * @param event Событие, произошедшее на экране списка чатов.
     */
    fun onEvent(event: ChatListScreenEvent) {
        when (event) {
            is ChatListScreenEvent.OnCreateChatClick -> {
                createChat(event.name)
            }

            is ChatListScreenEvent.OnOpenChatClick -> {
                commands.tryEmit(ChatListScreenCommand.OpenChat(event.chatId))
            }

            is ChatListScreenEvent.OnDeleteChatClick -> {
                deleteChat(event.chatId)
            }

            ChatListScreenEvent.OnOpenSettingsClick -> {
                commands.tryEmit(ChatListScreenCommand.OpenSettings)
            }
        }
    }

    private fun createChat(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return

        scope.launch(Dispatchers.IO) {
            val chat = Chat(
                id = UUID.randomUUID(),
                name = trimmed,
                time = System.currentTimeMillis(),
                parentId = null,
                taskContextId = null,
            )
            ragChatRepository.add(chat)
            commands.tryEmit(ChatListScreenCommand.OpenChat(chat.id))
        }
    }

    private fun deleteChat(chatId: UUID) {
        scope.launch(Dispatchers.IO) {
            ragChatRepository.deleteChat(chatId)
        }
    }
}
