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

/**
 * Холдер состояния экрана контекста задачи.
 * Предоставляет текущее состояние экрана через [StateFlow] и канал команд для взаимодействия с Compose-экраном.
 * Отвечает за загрузку данных из [TaskContextRepository]
 * и обработку пользовательских действий (например, нажатие "Назад").
 *
 * @property taskContextRepository репозиторий, из которого загружается контекст задачи.
 * @property scope CoroutineScope, используемый для запуска асинхронных операций.
 */
@Single
class TaskContextScreenStateHolder(
    private val taskContextRepository: TaskContextRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(TaskContextScreenState())
    /**
     * Неизменяемый поток текущего состояния экрана.
     * Содержит данные, необходимые для отображения: идентификаторы, объект контекста задачи и т.д.
     */
    val state: StateFlow<TaskContextScreenState> = _state.asStateFlow()

    /**
     * Канал для отправки одноразовых команд экрану (например, навигационных действий).
     * Имеет буферную ёмкость 1, что гарантирует, что команда не будет потеряна, если экран ещё не готов к приёму.
     */
    val commands = MutableSharedFlow<TaskContextScreenCommand>(
        extraBufferCapacity = 1,
    )

    /**
     * Принимает аргументы навигации экрана: идентификаторы контекста задачи и чата.
     * Обновляет состояние и запускает загрузку контекста задачи из репозитория в фоновом потоке.
     *
     * @param taskContextId идентификатор контекста задачи (пока используется для загрузки конкретного объекта).
     * @param chatId идентификатор чата, ассоциированного с этим экраном.
     */
    fun onArgs(taskContextId: UUID, chatId: UUID) {
        _state.update { it.copy(taskContextId = taskContextId, chatId = chatId) }

        scope.launch(Dispatchers.IO) {
            val taskContext = taskContextRepository.getTaskContext(taskContextId)
            _state.update { it.copy(taskContext = taskContext) }
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Назад".
     * Извлекает из текущего состояния идентификатор чата и, если он присутствует,
     * отправляет команду [TaskContextScreenCommand.Back] с этим идентификатором.
     */
    fun onBackClick() {
        val chatId = state.value.chatId ?: return
        commands.tryEmit(TaskContextScreenCommand.Back(chatId))
    }
}
