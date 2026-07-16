package com.github.mobdev778.aiadventchallenge.domain.agent.agents

import com.github.mobdev778.aiadventchallenge.domain.agent.AgentOrchestrator
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentContext
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentRequest
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentResponse

/**
 * Абстрактный базовый класс для всех агентов, реализующих логику обработки запросов в системе.
 *
 * Определяет контракт взаимодействия с оркестратором: принимает [AgentContext], [AgentRequest] и возвращает
 * [AgentResponse]. Конкретные реализации реализуют метод [handle], в котором на основе контекста задачи,
 * профиля пользователя, доступных инструментов и инвариантов генерируют ответ или цепочку вызовов
 * через [AgentOrchestrator.asyncRequest].
 *
 * @property id Уникальный идентификатор агента, используемый в логах и при маршрутизации.
 */
abstract class Agent(
    val id: String,
) {

    /**
     * Основной обработчик запроса, вызываемый оркестратором в контексте пула агентов.
     *
     * Метод получает полный контекст задачи и запрос от пользователя, выполняет всю необходимую
     * бизнес-логику и формирует итоговый или промежуточный ответ. Может быть приостановлен
     * (suspend) для выполнения асинхронных операций, таких как обращение к внешним инструментам.
     *
     * @param orchestrator Ссылка на оркестратор, через который можно отправлять дополнительные
     *   асинхронные запросы другим агентам ([AgentOrchestrator.asyncRequest]).
     * @param context Текущий контекст агента, включающий профиль, состояние задачи, историю окна,
     *   список инвариантов и доступные инструменты.
     * @param request Исходный запрос пользователя, содержащий текст, идентификаторы чата и задачи,
     *   а также метаданные.
     * @return Ответ агента ([AgentResponse]) с текстовым сообщением, ответами от инструментов,
     *   информацией о потреблённых токенах и (опционально) обновлённым контекстом задачи.
     */
    abstract suspend fun handle(
        orchestrator: AgentOrchestrator,
        context: AgentContext,
        request: AgentRequest
    ): AgentResponse

    /**
     * Возвращает краткое строковое представление агента в формате `[id] SimpleClassName`.
     *
     * @return Строка, содержащая идентификатор агента и простое имя класса.
     */
    override fun toString(): String {
        return "[$id] ${this.javaClass.simpleName}"
    }
}
