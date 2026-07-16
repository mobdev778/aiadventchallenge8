package com.github.mobdev778.aiadventchallenge.domain.contextmanagement

import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.tree.TreeBuilder
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.tree.TreeNode
import java.util.UUID

/**
 * Стратегия управления контекстом, не выполняющая сжатия диалога.
 *
 * Эта реализация [ContextManagementStrategy] возвращает полную основную
 * ветку диалога, представленную левым поддеревом диалогового дерева.
 *
 * Алгоритм работы: на основе списка сообщений строится бинарное дерево
 * с помощью [TreeBuilder], затем из корневого узла последовательно
 * обходятся левые дочерние узлы, формируя цепочку сообщений без
 * альтернативных ответвлений. Таким образом, итоговый список содержит
 * все сообщения вдоль "главного" пути диалога, сохраняя хронологический
 * порядок.
 *
 * Очистка данных чата (`clear`) в данной стратегии не производится.
 *
 * @see TreeBuilder
 * @see TreeNode
 */
class NoStrategy : ContextManagementStrategy {

    /**
     * Возвращает основную ветку диалога, полученную обходом левого
     * поддерева диалогового дерева.
     *
     * @param chatId Идентификатор чата, из которого извлекаются сообщения.
     *               В данной реализации не используется.
     * @param history Полный список сообщений чата, упорядоченный
     *                хронологически или в соответствии с логикой приложения.
     * @return Список сообщений, принадлежащих левой ветке диалога
     *         (без учёта альтернативных вариантов).
     */
    override suspend fun selectMessages(chatId: UUID, history: List<ChatMessage>): List<ChatMessage> {
        val root = TreeBuilder().messages(history).build()

        val result = ArrayList<ChatMessage>()
        var node: TreeNode? = root
        while (node != null) {
            result.add(node.value)
            node = node.left
        }

        return result
    }

    /**
     * Пустая операция очистки данных для указанного чата.
     *
     * Данная стратегия не управляет сохранением контекста,
     * поэтому метод не выполняет никаких действий.
     *
     * @param chatId Идентификатор чата, который нужно очистить.
     */
    override suspend fun clear(chatId: UUID) {
        // No op
    }
}
