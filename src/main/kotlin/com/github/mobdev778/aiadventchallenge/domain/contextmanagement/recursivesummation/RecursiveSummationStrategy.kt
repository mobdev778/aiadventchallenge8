package com.github.mobdev778.aiadventchallenge.domain.contextmanagement.recursivesummation

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagChatRepository
import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.ContextManagementStrategy
import java.util.UUID

/**
 * Рекурсивная суммаризация (временно не используется).
 */
class RecursiveSummationStrategy(
    private val maxMessages: Int,
    private val getSummaryUseCase: GetSummaryUseCase,
    private val chatRepository: RagChatRepository,
) : ContextManagementStrategy {

    override suspend fun selectMessages(
        chatId: UUID,
        history: List<ChatMessage>,
    ): List<ChatMessage> {
        val history = history.filter { it.parentId == null }

        return if (history.size < maxMessages) {
            history
        } else {
            compressMessages(history)
        }
    }

    /**
     * Выполняет сжатие истории сообщений через рекурсивную суммаризацию.
     *
     * Алгоритм:
     * 1. Выбирает левую половину сообщений (ранние сообщения).
     * 2. В этой половине находит несжатые сообщения (rank == 0, 1, ...).
     * 3. Если найдено только одно сообщение — повышает его ранг и возвращает историю как есть.
     * 4. Выполняет суммаризацию через [getSummaryUseCase].
     * 5. Повышает ранг summary, связывает исходные сообщения как дочерние.
     * 6. Сохраняет всё в БД и возвращает обновлённую историю.
     */
    @Suppress("UnusedParameter")
    private suspend fun compressMessages(
        history: List<ChatMessage>,
    ): List<ChatMessage> {
        // 1) выбираем левую, возможно бОльшую половину - ранние сообщения
        // Важно: мы не выбираем "history.take(maxMessages / 2)", потому что пользователь мог уменьшить
        // размер окна в настройках и для малых чисел нам придется делать слишком много сжатий "маленькой"
        // левой половины
        val leftHalf = history.take(history.size - maxMessages / 2)

        // 2) в этой половине ищем сообщения
        var uncompressed = emptyList<ChatMessage>()
        var rank = 0
        while (uncompressed.isEmpty()) {
            uncompressed = leftHalf.filter { it.rank == rank }
            rank++
        }

        // плохой кейс - в блоке "uncompressed" только 1 сообщение.
        // это означает, что мы уже "апнули" несколько сообщений левее, а текущее сжимать нет смысла -
        // потому что оно только одно.
        // В этом случае нам нужно просто поднять ранг оставшегося сообщения и начать заново:
        return if (uncompressed.size == 1) {
            chatRepository.add(uncompressed.first().copy(rank = rank))
            // возвращаем историю "как есть" предполагая, что на следующем цикле мы получим сжатие
            history
        } else {
            // 3) выполняем суммаризацию
            var summary = getSummaryUseCase.invoke(uncompressed)

            // 4) обрабатываем ошибки
            // TODO для упрощения пока в случае ошибки возвращаем исходную историю
            // TODO нужно будет переделать этот механизм - делать несколько попыток сжатия, а если не получилось,
            // пытаться просто применить "Sliding Window"
            if (summary == null) {
                history
            } else {
                // 5) повышаем ранг у "summary" - он должен быть на 1 выше, чем у детей
                summary = summary.copy(rank = rank)

                // 6) записываем "uncompressed" сообщения как "детей" summary
                uncompressed = uncompressed.map {
                    it.copy(parentId = summary.id)
                }

                // 7) сохраняем все в БД
                chatRepository.add(uncompressed + summary)

                // 8) возвращаем обновленный чат - удаляем блок из "сжатых сообщений" и добавляем "summary"
                val compressedIds = uncompressed.map { it.id }
                (history.filter { !compressedIds.contains(it.id) } + summary).sortedBy { it.time }
            }
        }
    }

    override suspend fun clear(chatId: UUID) {
        // No op
    }
}
