package com.github.mobdev778.aiadventchallenge.domain.task

import java.util.UUID

data class TaskContext(
    val id: UUID,           // идентификатор контекста
    val task: String,       // название задачи
    val state: TaskState,   // этап решения задачи
    val step: Int,          // шаг внутри этапа
    val plan: List<String>, // утвержденный план
    val done: List<String>, // что уже сделано
    val current: String,    // что делаем сейчас
)
