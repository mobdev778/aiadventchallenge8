package com.github.mobdev778.aiadventchallenge.presentation.taskcontextscreen

import com.github.mobdev778.aiadventchallenge.data.taskcontext.repository.TaskContextRepository
import com.github.mobdev778.aiadventchallenge.presentation.taskcontextscreen.model.TaskContextScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class TaskContextScreenStateHolder(
    private val taskContextRepository: TaskContextRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(TaskContextScreenState())
    val state: StateFlow<TaskContextScreenState> = _state.asStateFlow()

    val commands = MutableSharedFlow<TaskContextScreenCommand>(
        // Так команда дождется, пока Compose-экран будет готов ее принять.
        extraBufferCapacity = 1,
    )

    fun onArgs(taskContextId: UUID, chatId: UUID) {
        _state.update { it.copy(taskContextId = taskContextId, chatId = chatId) }

        scope.launch(Dispatchers.IO) {
            // Сейчас в репозитории хранится один TaskContext (id=0), поэтому просто читаем его.
            // taskContextId оставляем как входной аргумент (на будущее/для совместимости роутинга).
            val taskContext = taskContextRepository.getTaskContext(taskContextId)
            _state.update { it.copy(taskContext = taskContext) }
        }
    }

    fun onBackClick() {
        val chatId = state.value.chatId ?: return
        commands.tryEmit(TaskContextScreenCommand.Back(chatId))
    }
}
