package com.github.mobdev778.aiadventchallenge.presentation.taskcontextscreen

import java.util.UUID

/**
 * Запечатанный интерфейс, представляющий команды для экрана контекста задачи.
 * Используется для навигации и передачи действий от UI к логике экрана.
 */
sealed interface TaskContextScreenCommand {
    /**
     * Команда возврата на предыдущий экран.
     * @param chatId идентификатор чата, для которого выполняется возврат.
     */
    data class Back(val chatId: UUID) : TaskContextScreenCommand
}
