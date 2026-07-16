package com.github.mobdev778.aiadventchallenge.presentation.logsscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.logsscreen.composable.LogsFileScreenContent
import org.koin.java.KoinJavaComponent.inject

/**
 * Экран просмотра файла логов.
 *
 * Является точкой входа презентационного слоя для отображения содержимого лог-файла.
 * Отвечает за создание и запуск [LogsFileScreenStateHolder] (через Koin-инъекцию), который
 * управляет состоянием экрана и периодически считывает данные из файловой системы.
 *
 * При появлении на экране вызывает [LogsFileScreenStateHolder.startObserving], запуская
 * цикл обновления состояния в отдельной корутине. Полученное состояние передаётся в
 * [LogsFileScreenContent] для построения пользовательского интерфейса.
 */
@Composable
fun LogsFileScreen() {
    val stateHolder = remember {
        inject<LogsFileScreenStateHolder>(LogsFileScreenStateHolder::class.java).value
    }
    val state = stateHolder.state.collectAsState().value

    LaunchedEffect(Unit) {
        stateHolder.startObserving()
    }

    LogsFileScreenContent(state = state)
}
