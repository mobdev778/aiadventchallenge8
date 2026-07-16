package com.github.mobdev778.aiadventchallenge.domain.contextmanagement.slidingwindow

import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.ContextManagementStrategy
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.tree.TreeBuilder
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.tree.TreeNode
import java.util.UUID

/**
 * Стратегия управления контекстом на основе скользящего окна.
 *
 * Обирает последние [maxMessages] сообщений из истории чата, восстанавливая их хронологический
 * порядок с помощью [TreeBuilder]. Дерево диалога позволяет корректно обрабатывать ветвление
 * (в том числе альтернативные ответы ассистента), извлекая основную линию диалога через левые
 * дочерние узлы ([TreeNode.left]). Таким образом, окно ограничивает объём контекста,
 * используемый ассистентом, и предотвращает переполнение токенов при длительных диалогах.
 *
 * @property maxMessages Максимальное количество сообщений, которое остаётся в контексте.
 *                       Ограничивает размер возвращаемого списка при вызове [selectMessages].
 */
class SlidingWindowStrategy(
    /**
     * Максимальное количество сообщений, оставляемое в контексте.
     * Ограничивает размер возвращаемого списка при выборе сообщений.
     */
    val maxMessages: Int
) : ContextManagementStrategy {

    /**
     * Выбирает актуальные сообщения для текущего контекста диалога, применяя скользящее окно.
     *
     * Процесс включает следующие шаги:
     * 1. Построение бинарного дерева диалога из переданной [history] с помощью [TreeBuilder].
     * 2. Извлечение основной хронологической линии путём последовательного перехода
     *    по левым дочерним узлам (левый узел всегда содержит более раннее сообщение).
     * 3. Обрезка полученного списка до последних [maxMessages] элементов с помощью `takeLast`.
     *
     * Такой подход гарантирует, что возвращаются только последние сообщения в хронологическом
     * порядке, даже если история содержит альтернативные ветки.
     *
     * @param chatId Идентификатор чата. В данной стратегии не используется, но присутствует
     *               для соответствия интерфейсу [ContextManagementStrategy].
     * @param history Полный список сообщений чата, из которого необходимо выбрать подмножество.
     * @return Список сообщений длиной не более [maxMessages], представляющий последние
     *         по времени сообщения основной ветки диалога.
     */
    override suspend fun selectMessages(chatId: UUID, history: List<ChatMessage>): List<ChatMessage> {
        val root = TreeBuilder().messages(history).build()

        val result = ArrayList<ChatMessage>()
        var node: TreeNode? = root
        while (node != null) {
            result.add(node.value)
            node = node.left
        }

        return result.takeLast(maxMessages)
    }

    /**
     * Очищает данные для текущего чата.
     *
     * В данной реализации отсутствует состояние, связанное с конкретным чатом, поэтому
     * метод не выполняет никаких действий (заглушка).
     *
     * @param chatId Идентификатор чата, для которого требуется очистка.
     */
    override suspend fun clear(chatId: UUID) {
        // No op
    }
}
