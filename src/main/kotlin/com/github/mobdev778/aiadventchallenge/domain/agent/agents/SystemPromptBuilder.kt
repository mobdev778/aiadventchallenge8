package com.github.mobdev778.aiadventchallenge.domain.agent.agents

import com.github.mobdev778.aiadventchallenge.domain.invariant.Invariant
import com.github.mobdev778.aiadventchallenge.domain.profile.model.Profile
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import com.github.mobdev778.aiadventchallenge.domain.task.TaskState

class SystemPromptBuilder {

    private var query: String = ""
    private lateinit var ctx: TaskContext
    private lateinit var profile: Profile
    private lateinit var invariants: List<Invariant>
    private lateinit var agentRules: String

    fun query(query: String) = apply {
        this.query = query
    }

    fun context(ctx: TaskContext) = apply {
        this.ctx = ctx
    }

    fun profile(profile: Profile) = apply {
        this.profile = profile
    }

    fun invariants(invariants: List<Invariant>) = apply {
        this.invariants = invariants
    }

    fun agentRules(agentRules: String) = apply {
        this.agentRules = agentRules
    }

    fun build(): String {
        // Вычисляем общее количество шагов на основе размера плана
        val totalSteps = ctx.plan.size

        return """
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
                - Если step завершен - верни "[next_step]".
                - Строго соблюдай следующие инварианты проекта: ${invariants.joinToString(separator = ", ")}
        """.trimIndent()
    }
}