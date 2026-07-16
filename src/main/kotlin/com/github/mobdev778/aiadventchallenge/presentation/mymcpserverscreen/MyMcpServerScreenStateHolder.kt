package com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen

import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.MyMcpServerInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

private const val STATE_FLOW_TIMEOUT_MS = 5000L

/**
 * Холдер состояния экрана «Мои MCP-серверы», связывающий UI с доменным слоем MCP.
 *
 * Предоставляет реактивное состояние ([uiState]) на основе потока из [MyMcpServerInteractor]
 * и обрабатывает пользовательские события ([MyMcpServerScreenEvent]), транслируя их в команды
 * навигации или бизнес-операции.
 *
 * @param interactor Интерактор для управления MCP-серверами и получения потока их состояний.
 * @param scope Родительский корутинный скоуп, в котором запускаются асинхронные операции.
 */
@Single
class MyMcpServerScreenStateHolder(
    private val interactor: MyMcpServerInteractor,
    private val scope: CoroutineScope,
) {

    /**
     * Текущее состояние UI, полученное из потока состояний серверов [MyMcpServerInteractor.serverStatesFlow].
     *
     * Представляет собой [kotlinx.coroutines.flow.StateFlow], который удерживает список объектов состояния серверов.
     * При отсутствии активных подписчиков через [STATE_FLOW_TIMEOUT_MS] миллисекунд подписка на исходный поток
     * приостанавливается ([SharingStarted.WhileSubscribed]).
     */
    val uiState = interactor.serverStatesFlow
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(STATE_FLOW_TIMEOUT_MS),
            initialValue = emptyList(),
        )

    /**
     * Канал одноразовых команд навигации, например, для возврата на предыдущий экран.
     *
     * [MutableSharedFlow] с дополнительной буферизацией на один элемент ([extraBufferCapacity] = 1),
     * чтобы избежать потери команды, если она была испущена до появления подписчика.
     */
    val commands = MutableSharedFlow<MyMcpServerScreenCommand>(
        extraBufferCapacity = 1,
    )

    /**
     * Обрабатывает событие от пользователя на экране «Мои MCP-серверы».
     *
     * Каждое событие обрабатывается в фоновой корутине, принадлежащей переданному [scope].
     * В зависимости от типа события отправляется соответствующая навигационная команда в [commands]
     * или вызывается метод [MyMcpServerInteractor] для изменения состояния конкретного сервера.
     *
     * @param event Пользовательское событие, реализующее интерфейс [MyMcpServerScreenEvent].
     */
    fun onEvent(event: MyMcpServerScreenEvent) {
        scope.launch {
            when (event) {
                is MyMcpServerScreenEvent.OnBackClick -> {
                    commands.emit(MyMcpServerScreenCommand.Back)
                }

                is MyMcpServerScreenEvent.OnStartClick -> {
                    interactor.start(event.name)
                }

                is MyMcpServerScreenEvent.OnStopClick -> {
                    interactor.stop(event.name)
                }
            }
        }
    }
}
