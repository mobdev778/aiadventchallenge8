package com.github.mobdev778.aiadventchallenge.domain.agent.pool

import com.github.mobdev778.aiadventchallenge.domain.agent.AgentOrchestrator
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentContext
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentRequest
import kotlinx.coroutines.channels.Channel

/**
 * Неограниченная очередь запросов для пула агентов.
 *
 * Реализует асинхронную передачу заданий между [AgentOrchestrator] и агентами-исполнителями.
 * Использует внутренний [Channel] с неограниченной ёмкостью, что гарантирует отсутствие блокировки
 * отправителя при постановке элемента в очередь ([enqueue]).
 *
 * Каждый элемент очереди — это тройка:
 * - отправитель-оркестратор [AgentOrchestrator],
 * - полный контекст выполнения [AgentContext],
 * - пользовательский запрос [AgentRequest].
 *
 * Очередь поддерживает корректное завершение работы через вызов [close], после которого
 * невозможно добавить новые элементы, а чтение из очереди завершится после обработки всех ранее
 * поставленных заданий.
 */
class AgentRequestQueue {

    private val channel = Channel<Triple<AgentOrchestrator, AgentContext, AgentRequest>>(Channel.UNLIMITED)

    /**
     * Помещает задание в очередь.
     *
     * Задание добавляется без приостановки вызывающего потока (неблокирующая операция), поскольку
     * канал имеет неограниченную ёмкость. Очередь гарантирует сохранение порядка поступления заданий.
     *
     * @param agentOrchestrator оркестратор, инициировавший запрос. Используется для обратной связи
     *                          или маршрутизации ответа после обработки агентом.
     * @param context полный контекст выполнения агента, включающий профиль пользователя,
     *                контекст задачи, историю сообщений и доступные инструменты.
     * @param message исходный запрос от пользователя или системы.
     */
    suspend fun enqueue(agentOrchestrator: AgentOrchestrator, context: AgentContext, message: AgentRequest) {
        channel.send(Triple(agentOrchestrator, context, message))
    }

    /**
     * Извлекает следующее задание из очереди.
     *
     * Если очередь пуста, вызывающая корутина будет приостановлена до момента появления
     * нового элемента. Извлечение происходит в порядке поступления (FIFO).
     *
     * @return тройка, содержащая оркестратора, контекст и запрос, которые были помещены в очередь.
     */
    suspend fun dequeue(): Triple<AgentOrchestrator, AgentContext, AgentRequest> {
        return channel.receive()
    }

    /**
     * Закрывает очередь для приёма новых заданий.
     *
     * После вызова этого метода дальнейшие попытки [enqueue] не будут приводить к добавлению
     * элементов в очередь, а читающие корутины после обработки оставшихся элементов получат
     * сигнал о закрытии канала.
     *
     * Рекомендуется вызывать при завершении работы пула агентов для корректной остановки
     * всех связанных корутин.
     */
    fun close() {
        channel.close()
    }
}
