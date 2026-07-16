package com.github.mobdev778.aiadventchallenge.presentation.taskcontextscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.taskcontextscreen.composable.TaskContextScreenContent
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

/**
 * Экран контекста задачи.
 *
 * Отображает детали задачи и обеспечивает взаимодействие с пользователем.
 * Использует [TaskContextScreenStateHolder] для управления состоянием и навигацией.
 *
 * @param taskContextId Идентификатор контекста задачи.
 * @param chatId Идентификатор чата, связанного с задачей.
 * @param onBack Колбэк для обработки события возврата на предыдущий экран.
 *               Принимает идентификатор чата, для которого выполняется возврат.
 */
@Composable
fun TaskContextScreen(
    taskContextId: UUID,
    chatId: UUID,
    onBack: (chatId: UUID) -> Unit,
) {
    val stateHolder = remember(chatId) {
        inject<TaskContextScreenStateHolder>(TaskContextScreenStateHolder::class.java).value
    }

    val state by stateHolder.state.collectAsState()

    TaskContextScreenContent(
        state = state,
        onBack = { stateHolder.onBackClick() },
    )

    LaunchedEffect(taskContextId, chatId) {
        stateHolder.onArgs(taskContextId = taskContextId, chatId = chatId)
    }

    LaunchedEffect(chatId) {
        stateHolder.commands.collect { command ->
            when (command) {
                is TaskContextScreenCommand.Back -> onBack(command.chatId)
            }
        }
    }
}
