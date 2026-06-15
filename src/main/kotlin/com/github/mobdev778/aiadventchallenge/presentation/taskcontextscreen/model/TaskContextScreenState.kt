package com.github.mobdev778.aiadventchallenge.presentation.taskcontextscreen.model

import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import java.util.UUID

data class TaskContextScreenState(
    val taskContextId: UUID? = null,
    val chatId: UUID? = null,
    val taskContext: TaskContext? = null,
)
