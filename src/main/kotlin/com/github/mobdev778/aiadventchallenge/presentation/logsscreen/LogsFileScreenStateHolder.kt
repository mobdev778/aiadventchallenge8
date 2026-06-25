package com.github.mobdev778.aiadventchallenge.presentation.logsscreen

import com.github.mobdev778.aiadventchallenge.infrastructure.logging.PluginLogFileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
class LogsFileScreenStateHolder(
    private val pluginLogFileManager: PluginLogFileManager,
    private val scope: CoroutineScope,
) {

    private val _state = MutableStateFlow(LogsFileScreenState())
    val state: StateFlow<LogsFileScreenState> = _state.asStateFlow()

    private var started = false

    fun startObserving() {
        if (started) return
        started = true

        scope.launch(Dispatchers.IO) {
            while (isActive) {
                val file = pluginLogFileManager.currentLogFile()
                val content = pluginLogFileManager.readCurrentDayLogs()
                _state.value = LogsFileScreenState(
                    fileName = file.fileName.toString(),
                    logs = if (content.isBlank()) emptyList() else content.lines(),
                )
                delay(1_000)
            }
        }
    }
}
