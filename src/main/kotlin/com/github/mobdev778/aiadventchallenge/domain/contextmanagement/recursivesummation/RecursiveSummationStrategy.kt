package com.github.mobdev778.aiadventchallenge.domain.contextmanagement.recursivesummation

import com.github.mobdev778.aiadventchallenge.data.chat.repository.ChatRepository
import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.ContextManagementStrategy
import java.util.UUID

/**
 * Рекурсивная суммаризация (временно не используется).
 */
class RecursiveSummationStrategy(
    private val maxMessages: Int,
    private val getSummaryUseCase: GetSummaryUseCase,
    private val chatRepository: ChatRepository,
) : ContextManagementStrategy {

    override suspend fun selectMessages(
        chatId: UUID,
        history: List<ChatMessage>,
    ): List<ChatMessage> {
        val history = history.filter { it.parentId == null }

        // если лимит по сообщениям пока не достигнут - все ок, двигаемся дальше
        if (history.size < maxMessages) return history

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
        if (uncompressed.size == 1) {
            chatRepository.add(uncompressed.first().copy(rank = rank))
            // возвращаем историю "как есть" предполагая, что на следующем цикле мы получим сжатие
            return history
        }

        // 3) выполняем суммаризацию
        var summary = getSummaryUseCase.invoke(uncompressed)

        // 4) обрабатываем ошибки
        // TODO для упрощения пока в случае ошибки возвращаем исходную историю
        // TODO нужно будет переделать этот механизм - делать несколько попыток сжатия, а если не получилось,
        // пытаться просто применить "Sliding Window"
        if (summary == null) return history

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

        return (history.filter { !compressedIds.contains(it.id) } + summary).sortedBy { it.time }
    }

    override suspend fun clear(chatId: UUID) {
        // No op
    }
}