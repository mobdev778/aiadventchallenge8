package com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.composable.MyMcpServerScreenContent
import org.koin.java.KoinJavaComponent.inject

/**
 * Основной Composable-экран «Мои MCP-серверы».
 *
 * Функция обеспечивает отображение интерфейса экрана, связывая UI-состояние, полученное от
 * [MyMcpServerScreenStateHolder], с визуальными компонентами внутри [MyMcpServerScreenContent].
 * Также обрабатывает команды, поступающие от StateHolder, например, навигацию назад
 * ([MyMcpServerScreenCommand.Back]).
 *
 * @param onBack лямбда, вызываемая при необходимости вернуться на предыдущий экран.
 */
@Composable
fun MyMcpServerScreen(
    onBack: () -> Unit,
) {
    val stateHolder = remember {
        inject<MyMcpServerScreenStateHolder>(MyMcpServerScreenStateHolder::class.java).value
    }
    val state by stateHolder.uiState.collectAsState()

    MyMcpServerScreenContent(
        state = state,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(Unit) {
        stateHolder.commands.collect { command ->
            when (command) {
                MyMcpServerScreenCommand.Back -> onBack()
            }
        }
    }
}
