package com.github.mobdev778.aiadventchallenge.presentation.taskcontextscreen

import java.util.UUID

sealed interface TaskContextScreenCommand {
    data class Back(val chatId: UUID) : TaskContextScreenCommand
}
