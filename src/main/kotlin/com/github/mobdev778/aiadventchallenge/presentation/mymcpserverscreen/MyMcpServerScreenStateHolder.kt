package com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen

import com.github.mobdev778.aiadventchallenge.data.mymcpserver.repository.MyMcpServerConfigRepository
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.MyMcpServerInteractor
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.model.MyMcpServer
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.model.MyMcpServerConfig
import com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.model.MyMcpServerScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
class MyMcpServerScreenStateHolder(
    private val myMcpServerInteractor: MyMcpServerInteractor,
    private val scope: CoroutineScope,
) {

    private val defaultConfig = MyMcpServerConfigRepository.default
    private val draftFlow = MutableStateFlow(defaultConfig)
    private val portInputFlow = MutableStateFlow(defaultConfig.port.toString())

    private val savedConfigFlow = myMcpServerInteractor
        .observeConfig()
        .onEach { config ->
            if (draftFlow.value == defaultConfig && portInputFlow.value == defaultConfig.port.toString()) {
                draftFlow.value = config
                portInputFlow.value = config.port.toString()
            }
        }

    val uiState: StateFlow<MyMcpServerScreenState> = combine(
        savedConfigFlow,
        draftFlow,
        portInputFlow,
        myMcpServerInteractor.observeServer(),
    ) { savedConfig: MyMcpServerConfig, draftConfig: MyMcpServerConfig, portInput: String, server: MyMcpServer ->
        MyMcpServerScreenState(
            savedConfig = savedConfig,
            draftConfig = draftConfig,
            portInput = portInput,
            server = server,
            actionEnabled = savedConfig != draftConfig,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MyMcpServerScreenState(),
    )

    val commands = MutableSharedFlow<MyMcpServerScreenCommand>(
        extraBufferCapacity = 1,
    )

    fun onEvent(event: MyMcpServerScreenEvent) {
        when (event) {
            MyMcpServerScreenEvent.OnBackClick -> {
                commands.tryEmit(MyMcpServerScreenCommand.Back)
            }

            is MyMcpServerScreenEvent.OnLaunchAtStartupChanged -> {
                draftFlow.update { it.copy(launchAtStartup = event.value) }
            }

            is MyMcpServerScreenEvent.OnPortChanged -> {
                portInputFlow.value = event.value
                event.value.toIntOrNull()?.let { port ->
                    draftFlow.update { it.copy(port = port) }
                }
            }

            MyMcpServerScreenEvent.OnSaveClick -> {
                save()
            }

            MyMcpServerScreenEvent.OnResetClick -> {
                reset()
            }

            MyMcpServerScreenEvent.OnStartStopClick -> {
                toggleServer()
            }
        }
    }

    private fun save() {
        val draft = draftFlow.value
        scope.launch {
            myMcpServerInteractor.saveConfig(draft)
        }
    }

    private fun reset() {
        val saved = uiState.value.savedConfig
        draftFlow.value = saved
        portInputFlow.value = saved.port.toString()
    }

    private fun toggleServer() {
        scope.launch {
            if (uiState.value.server.isRunning) {
                myMcpServerInteractor.stop()
            } else {
                myMcpServerInteractor.start()
            }
        }
    }
}
