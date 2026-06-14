package com.github.mobdev778.aiadventchallenge.presentation.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen.ChatListScreen
import com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen.ChatListScreenStateHolder
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreen
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenStateHolder
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreen
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreenStateHolder
import java.util.UUID

@Immutable
sealed interface Screen {
    data object ChatList : Screen
    data class Chat(val chatId: UUID) : Screen
    data object Settings : Screen
}

class AppRouter(initial: Screen = Screen.ChatList) {
    var screen: Screen by mutableStateOf(initial)
        private set

    fun openChatList() {
        screen = Screen.ChatList
    }

    fun openChat(chatId: UUID) {
        screen = Screen.Chat(chatId)
    }

    fun openSettings() {
        screen = Screen.Settings
    }
}

@Composable
fun rememberAppRouter(initial: Screen = Screen.ChatList): AppRouter = remember { AppRouter(initial) }

@Composable
fun AppRouterContent(
    router: AppRouter,
    chatListScreenStateHolder: ChatListScreenStateHolder,
    chatScreenStateHolderFactory: (UUID) -> ChatScreenStateHolder,
    settingsScreenStateHolder: SettingsScreenStateHolder,
) {
    when (val s = router.screen) {
        is Screen.ChatList -> {
            ChatListScreen(
                stateHolder = chatListScreenStateHolder,
                onOpenChat = { router.openChat(it) },
                onOpenSettings = { router.openSettings() },
            )
        }

        is Screen.Chat -> {
            ChatScreen(
                chatId = s.chatId,
                stateHolder = chatScreenStateHolderFactory(s.chatId),
                onBack = { router.openChatList() },
                onOpenSettings = { router.openSettings() },
            )
        }

        is Screen.Settings -> {
            SettingsScreen(
                stateHolder = settingsScreenStateHolder,
                onBack = { router.openChatList() },
            )
        }
    }
}
