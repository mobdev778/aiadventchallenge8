package com.github.mobdev778.aiadventchallenge.presentation.addprofilescreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.addprofilescreen.composable.AddProfileScreenContent
import org.koin.java.KoinJavaComponent.inject

/**
 * Корневая composable-функция экрана добавления нового профиля.
 *
 * Отвечает за создание и привязку [AddProfileScreenStateHolder] через DI (Koin),
 * подписку на текущее состояние экрана и передачу его в [AddProfileScreenContent].
 * Также обрабатывает команды, поступающие от [AddProfileScreenStateHolder.commands],
 * преобразуя [AddProfileScreenCommand.Back] в вызов колбека [onBack] для навигации назад.
 *
 * @param onBack лямбда, вызываемая при необходимости вернуться на предыдущий экран.
 */
@Composable
fun AddProfileScreen(
    onBack: () -> Unit,
) {
    val stateHolder = remember {
        inject<AddProfileScreenStateHolder>(AddProfileScreenStateHolder::class.java).value
    }

    val state by stateHolder.state.collectAsState()

    AddProfileScreenContent(
        state = state,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(Unit) {
        stateHolder.commands.collect { command ->
            when (command) {
                AddProfileScreenCommand.Back -> onBack()
            }
        }
    }
}
