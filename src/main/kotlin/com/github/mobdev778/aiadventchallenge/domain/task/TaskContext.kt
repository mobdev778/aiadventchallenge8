package com.github.mobdev778.aiadventchallenge.domain.task

import java.util.UUID

/**
 * Контекст выполнения задачи, содержащий всю информацию о текущем состоянии процесса решения.
 *
 * @param id уникальный идентификатор контекста.
 * @param task название задачи.
 * @param state текущий этап решения задачи.
 * @param step номер текущего шага внутри этапа.
 * @param plan утвержденный план выполнения.
 * @param done список выполненных шагов.
 * @param current описание текущего выполняемого шага.
 */
data class TaskContext(
    val id: UUID,
    val task: String,
    val state: TaskState,
    val step: Int,
    val plan: List<String>,
    val done: List<String>,
    val current: String,
)
