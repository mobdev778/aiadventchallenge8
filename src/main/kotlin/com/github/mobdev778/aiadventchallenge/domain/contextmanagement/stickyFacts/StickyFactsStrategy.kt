package com.github.mobdev778.aiadventchallenge.domain.contextmanagement.stickyFacts

import com.github.mobdev778.aiadventchallenge.data.chat.repository.StickyFactsRepository
import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagChatRepository
import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.chat.model.MessageType
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.ContextManagementStrategy
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.tree.TreeBuilder
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.tree.TreeNode
import java.util.UUID

/**
 * Стратегия управления контекстом на основе "липких" фактов (sticky facts).
 *
 * Реализует [ContextManagementStrategy], используя механизм закреплённых фактов для сохранения
 * контекстной информации при превышении лимита сообщений в истории диалога. Стратегия
 * анализирует дерево диалога, выделяет новые сообщения, не отмеченные как обработанные,
 * извлекает из них ключевую информацию, объединяет с существующими фактами и сохраняет
 * компактное представление. Возвращает ограниченное количество последних сообщений
 * вместе с актуальным сообщением фактов.
 *
 * @param getStickyFactsUseCase use-case извлечения устойчивых фактов из набора сообщений.
 * @param convertTextToMapUseCase use-case преобразования строкового представления фактов
 *                                в отображение ключ-значение.
 * @param convertMapToTextUseCase use-case преобразования карты фактов обратно в строку.
 * @param maxMessages максимальное количество последних сообщений (кроме сообщения фактов),
 *                    возвращаемых в контексте.
 * @param stickyFactsRepository репозиторий для отслеживания обработанных сообщений.
 * @param chatRepository репозиторий чата, используемый для сохранения обновлённого сообщения фактов.
 */
class StickyFactsStrategy(
    private val getStickyFactsUseCase: GetStickyFactsUseCase,
    private val convertTextToMapUseCase: ConvertTextToMapUseCase,
    private val convertMapToTextUseCase: ConvertMapToTextUseCase,
    private val maxMessages: Int,
    private val stickyFactsRepository: StickyFactsRepository,
    private val chatRepository: RagChatRepository,
) : ContextManagementStrategy {

    /**
     * Выбирает сообщения для текущего контекста на основе стратегии sticky facts.
     *
     * Алгоритм:
     * 1. Находит или создаёт сообщение типа [MessageType.StickyFacts].
     * 2. Строит бинарное дерево диалога и извлекает основную ветку сообщений.
     * 3. Фильтрует ещё не обработанные сообщения (исключая сообщения фактов).
     * 4. Если есть новые сообщения, извлекает из них факты, объединяет с имеющимися
     *    и обновляет sticky-сообщение.
     * 5. Отмечает обработанные сообщения в [stickyFactsRepository] и сохраняет обновлённое
     *    sticky-сообщение через [chatRepository].
     * 6. Возвращает sticky-сообщение и последние [maxMessages] сообщений из основной ветки.
     *
     * @param chatId идентификатор чата.
     * @param history полная история сообщений чата.
     * @return список сообщений для включения в контекст модели.
     */
    override suspend fun selectMessages(chatId: UUID, history: List<ChatMessage>): List<ChatMessage> {
        var stickyMessage = history.firstOrNull { it.type == MessageType.StickyFacts } ?: ChatMessage(
            id = UUID.randomUUID(),
            chatId = chatId,
            type = MessageType.StickyFacts,
            parentId = null,
            branchB = false,
            text = " - нет сообщений -",
            time = 0, // наше сообщение всегда должно быть самым первым в списке
            tokens = 0,
            rank = 0,
        )

        val root = TreeBuilder().messages(history).build()

        val result = ArrayList<ChatMessage>()
        var node: TreeNode? = root
        while (node != null) {
            result.add(node.value)
            node = node.left
        }

        val nonStickyMessages = result.filter {
            it.type != MessageType.StickyFacts && !stickyFactsRepository.containsMessage(chatId, it.id)
        }

        if (nonStickyMessages.isNotEmpty()) {
            val oldStickyMessageMap = convertTextToMapUseCase.invoke(stickyMessage.text).toMutableMap()

            val stickyFacts = getStickyFactsUseCase.invoke(nonStickyMessages)
            val stickyFactsMap = convertTextToMapUseCase.invoke(stickyFacts?.text ?: "")
            val joinedStickyFacts = convertMapToTextUseCase.invoke(oldStickyMessageMap + stickyFactsMap)
            stickyMessage = stickyMessage.copy(
                time = 0,
                text = joinedStickyFacts,
            )
            // помечаем сначала сообщения как сообщения для игнорирования, чтобы не запускать цикл заново
            nonStickyMessages.forEach {
                stickyFactsRepository.addMessage(chatId, it.id)
            }
            // обновляем репозиторий
            chatRepository.add(stickyMessage)
        }

        return listOf(stickyMessage) + result.filter { it.type != MessageType.StickyFacts }.takeLast(maxMessages)
    }

    /**
     * Очищает все данные о закреплённых фактах для заданного чата.
     *
     * Делегирует вызов [StickyFactsRepository.clear], удаляя информацию об обработанных
     * сообщениях для указанного [chatId].
     *
     * @param chatId идентификатор чата.
     */
    override suspend fun clear(chatId: UUID) {
        stickyFactsRepository.clear(chatId)
    }
}
