package com.github.mobdev778.aiadventchallenge.domain.agent.agents

import com.github.mobdev778.aiadventchallenge.domain.invariant.Invariant
import com.github.mobdev778.aiadventchallenge.domain.profile.model.Profile
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import java.util.UUID

/**
 * Строитель системного промпта (SystemPromptBuilder), использующий шаблон "строитель" (Builder)
 * с плавным интерфейсом для пошагового формирования текстового промпта, отправляемого агенту.
 *
 * Промпт агрегирует информацию из контекста задачи ([TaskContext]), активного профиля ([Profile]),
 * бизнес-инвариантов ([Invariant]) и правил поведения агента.
 * Используется для подготовки инструкции LLM к очередному шагу решения задачи.
 */
class SystemPromptBuilder {

    private var chatId: UUID? = null
    private var query: String = ""
    private lateinit var ctx: TaskContext
    private lateinit var profile: Profile
    private lateinit var invariants: List<Invariant>
    private lateinit var agentRules: String

    /**
     * Устанавливает идентификатор чата.
     *
     * @param chatId уникальный идентификатор чата, в рамках которого ведётся диалог.
     * @return текущий экземпляр [SystemPromptBuilder] для возможности цепочечного вызова.
     */
    fun chatId(chatId: UUID) = apply {
        this.chatId = chatId
    }

    /**
     * Устанавливает текст текущего запроса пользователя.
     *
     * @param query текст запроса.
     * @return текущий экземпляр [SystemPromptBuilder] для цепочечного вызова.
     */
    fun query(query: String) = apply {
        this.query = query
    }

    /**
     * Устанавливает контекст задачи, содержащий состояние, шаги, план и выполненные действия.
     *
     * @param ctx объект [TaskContext] с деталями текущего этапа решения задачи.
     * @return текущий экземпляр [SystemPromptBuilder] для цепочечного вызова.
     */
    fun context(ctx: TaskContext) = apply {
        this.ctx = ctx
    }

    /**
     * Устанавливает активный профиль пользователя, определяющий стиль и правила поведения ассистента.
     *
     * @param profile объект [Profile] с содержимым инструкций.
     * @return текущий экземпляр [SystemPromptBuilder] для цепочечного вызова.
     */
    fun profile(profile: Profile) = apply {
        this.profile = profile
    }

    /**
     * Устанавливает список инвариантов (правил валидации), которые должны соблюдаться при формировании ответа.
     *
     * @param invariants коллекция [Invariant], определяющих ограничения на ответ.
     * @return текущий экземпляр [SystemPromptBuilder] для цепочечного вызова.
     */
    fun invariants(invariants: List<Invariant>) = apply {
        this.invariants = invariants
    }

    /**
     * Устанавливает дополнительные правила поведения агента, заданные в виде строки.
     *
     * @param agentRules строка с инструкциями для агента.
     * @return текущий экземпляр [SystemPromptBuilder] для цепочечного вызова.
     */
    fun agentRules(agentRules: String) = apply {
        this.agentRules = agentRules
    }

    /**
     * Собирает итоговый системный промпт на основе всех ранее переданных компонентов.
     *
     * В результирующую строку включаются идентификатор чата, текущее состояние задачи
     * (этап, номер шага / общее количество шагов, текущее действие, план, выполненные шаги),
     * содержимое профиля, запрос пользователя, правила агента и описание инвариантов.
     *
     * @return строка системного промпта для использования в запросе к LLM.
     */
    fun build(): String {
        // Вычисляем общее количество шагов на основе размера плана
        val totalSteps = ctx.plan.size

        return """
            chatId: \"$chatId\"
            [STATE] ${ctx.state}, step ${ctx.step}/$totalSteps
            [CURRENT] ${ctx.current}
            [PLAN] ${ctx.plan.joinToString(separator = ", ")}
            [DONE] ${ctx.done.joinToString(separator = ", ")}
            [PROFILE] ${profile.content}
            [QUERY] $query

            Rules:
                - Работай только в рамках current step
                - Не перепрыгивай этапы
                - $agentRules
                - Строго соблюдай следующие инварианты проекта: ${invariants.joinToString(separator = ", ")}
        """.trimIndent()
    }
}
