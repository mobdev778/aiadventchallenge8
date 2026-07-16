package com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen.composable.ChatListScreenContent
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

/**
 * Composable-функция экрана списка чатов.
 *
 * Отвечает за композицию UI и связывание управления состоянием с визуальным представлением.
 * Использует внедрение зависимостей (Koin) для получения [ChatListScreenStateHolder],
 * который предоставляет состояние чатов и обрабатывает пользовательские события.
 * Также подписывается на команды от stateHolder с помощью [LaunchedEffect],
 * преобразуя их в вызовы переданных колбэков для навигации.
 *
 * @param onOpenChat Колбэк, вызываемый при необходимости открыть конкретный чат.
 *                   Принимает [UUID] идентификатор чата.
 * @param onOpenSettings Колбэк, вызываемый при необходимости открыть экран настроек.
 */
@Composable
fun ChatListScreen(
    onOpenChat: (UUID) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val stateHolder = remember {
        inject<ChatListScreenStateHolder>(ChatListScreenStateHolder::class.java).value
    }
    val chats by stateHolder.chats.collectAsState()

    ChatListScreenContent(
        chats = chats,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(stateHolder.commands) {
        stateHolder.commands.collect { command ->
            when (command) {
                ChatListScreenCommand.OpenSettings -> {
                    onOpenSettings()
                }

                is ChatListScreenCommand.OpenChat -> {
                    onOpenChat(command.chatId)
                }
            }
        }
    }
}
