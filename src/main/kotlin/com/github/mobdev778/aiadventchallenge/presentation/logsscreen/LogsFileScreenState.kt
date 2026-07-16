package com.github.mobdev778.aiadventchallenge.presentation.logsscreen

/**
 * Состояние экрана просмотра файла логов.
 *
 * Содержит имя отображаемого файла и его содержимое в виде списка строк.
 * Используется в архитектуре presentation-слоя для хранения и передачи
 * UI-состояния экрана логов.
 *
 * @property fileName Имя файла логов, отображаемого на экране.
 * @property logs Список строк содержимого файла логов. Каждая строка соответствует
 *                одной записи лога.
 */
data class LogsFileScreenState(
    val fileName: String = "",
    val logs: List<String> = emptyList(),
)
