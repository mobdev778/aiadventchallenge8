package com.github.mobdev778.aiadventchallenge.presentation.taskcontextscreen.model

import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import java.util.UUID

/**
 * Состояние экрана контекста задачи. Содержит все данные, необходимые для отображения экрана,
 * включая идентификатор контекста задачи, идентификатор чата и сам объект контекста задачи.
 *
 * @property taskContextId Идентификатор контекста задачи. Может быть null, если контекст ещё не создан или не выбран.
 * @property chatId Идентификатор чата, связанного с контекстом задачи. Может быть null, если чат не инициализирован.
 * @property taskContext Объект контекста задачи, полученный из доменного слоя. Может быть null, если данные ещё не загружены.
 */
data class TaskContextScreenState(
    val taskContextId: UUID? = null,
    val chatId: UUID? = null,
    val taskContext: TaskContext? = null,
)
