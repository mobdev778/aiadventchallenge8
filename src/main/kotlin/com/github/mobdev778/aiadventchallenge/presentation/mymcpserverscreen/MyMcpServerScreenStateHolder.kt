package com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen

import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.MyMcpServerInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

private const val STATE_FLOW_TIMEOUT_MS = 5000L

@Single
class MyMcpServerScreenStateHolder(
    private val interactor: MyMcpServerInteractor,
    private val scope: CoroutineScope,
) {

    val uiState  = interactor.serverStatesFlow
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(STATE_FLOW_TIMEOUT_MS),
            initialValue = emptyList(),
        )

    val commands = MutableSharedFlow<MyMcpServerScreenCommand>(
        extraBufferCapacity = 1,
    )

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
