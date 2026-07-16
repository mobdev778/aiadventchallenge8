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

/**
 * Интервал опроса лог-файла в миллисекундах (1 секунда).
 */
private const val LOG_POLLING_INTERVAL_MS = 1_000L

/**
 * Компонент-синглтон, управляющий состоянием экрана просмотра логов.
 *
 * Периодически считывает содержимое лог-файла текущего дня через [PluginLogFileManager]
 * и обновляет [LogsFileScreenState], которое используется UI-слоем для отображения.
 * Наблюдение запускается однократно вызовом [startObserving] и продолжается до отмены корутины.
 *
 * @param pluginLogFileManager Менеджер лог-файлов, предоставляющий путь и содержимое файла.
 * @param scope Корутин-скоуп, в котором выполняется цикл опроса (обычно presentation-скоуп экрана).
 *
 * @property state Публичный [StateFlow] текущего состояния экрана логов.
 */
@Single
class LogsFileScreenStateHolder(
    private val pluginLogFileManager: PluginLogFileManager,
    private val scope: CoroutineScope,
) {

    private val _state = MutableStateFlow(LogsFileScreenState())
    val state: StateFlow<LogsFileScreenState> = _state.asStateFlow()

    /** Флаг, предотвращающий повторный запуск наблюдения. */
    private var started = false

    /**
     * Запускает периодическое чтение лог-файла и обновление UI-состояния.
     *
     * Если наблюдение уже было запущено, повторный вызов игнорируется.
     * Опрос ведётся в фоновом потоке ([Dispatchers.IO]) с интервалом [LOG_POLLING_INTERVAL_MS].
     */
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
                delay(LOG_POLLING_INTERVAL_MS)
            }
        }
    }
}
