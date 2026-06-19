package com.github.mobdev778.aiadventchallenge.domain.chat

import com.github.mobdev778.aiadventchallenge.domain.profile.model.Profile
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import com.github.mobdev778.aiadventchallenge.domain.task.TaskState

class SystemPromptBuilder {

    private var query: String = ""
    private lateinit var ctx: TaskContext
    private lateinit var profile: Profile

    fun query(query: String) = apply {
        this.query = query
    }

    fun context(ctx: TaskContext) = apply {
        this.ctx = ctx
    }

    fun profile(profile: Profile) = apply {
        this.profile = profile
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
                - Если step завершен - верни "[next_step]".
                - для ${TaskState.PrintResult} всегда возвращай "[next_step]". 
        """.trimIndent()
    }
}