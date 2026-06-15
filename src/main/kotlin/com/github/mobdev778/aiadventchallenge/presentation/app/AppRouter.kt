package com.github.mobdev778.aiadventchallenge.presentation.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.mobdev778.aiadventchallenge.presentation.addprofilescreen.AddProfileScreen
import com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen.ChatListScreen
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreen
import com.github.mobdev778.aiadventchallenge.presentation.editprofilescreen.EditProfileScreen
import com.github.mobdev778.aiadventchallenge.presentation.profilelistscreen.ProfileListScreen
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreen
import com.github.mobdev778.aiadventchallenge.presentation.taskcontextscreen.TaskContextScreen
import java.util.UUID

@Immutable
sealed interface Screen {
    data object ChatList : Screen
    data class Chat(val chatId: UUID) : Screen
    data object Settings : Screen
    data object ProfileList : Screen
    data object AddProfile : Screen
    data class EditProfile(val profileId: UUID) : Screen
    data class TaskContext(val taskContextId: UUID, val chatId: UUID) : Screen
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

    fun openProfileList() {
        screen = Screen.ProfileList
    }

    fun openAddProfile() {
        screen = Screen.AddProfile
    }

    fun openEditProfile(profileId: UUID) {
        screen = Screen.EditProfile(profileId)
    }

    fun openTaskContext(taskContextId: UUID, chatId: UUID) {
        screen = Screen.TaskContext(taskContextId = taskContextId, chatId = chatId)
    }
}

@Composable
fun rememberAppRouter(initial: Screen = Screen.ChatList): AppRouter = remember { AppRouter(initial) }

@Composable
fun AppRouterContent(
    router: AppRouter,
) {
    when (val s = router.screen) {
        is Screen.ChatList -> {
            ChatListScreen(
                onOpenChat = { router.openChat(it) },
                onOpenSettings = { router.openSettings() },
                onOpenProfiles = { router.openProfileList() },
            )
        }

        is Screen.Chat -> {
            ChatScreen(
                chatId = s.chatId,
                onBack = { router.openChatList() },
                onOpenSettings = { router.openSettings() },
                onOpenTaskContext = { taskContextId, chatId ->
                    router.openTaskContext(taskContextId = taskContextId, chatId = chatId)
                },
            )
        }

        is Screen.Settings -> {
            SettingsScreen(
                onBack = { router.openChatList() },
            )
        }

        is Screen.ProfileList -> {
            ProfileListScreen(
                onBack = { router.openChatList() },
                onOpenAddProfile = { router.openAddProfile() },
                onOpenEditProfile = { router.openEditProfile(it) },
            )
        }

        is Screen.AddProfile -> {
            AddProfileScreen(
                onBack = { router.openProfileList() },
            )
        }

        is Screen.EditProfile -> {
            EditProfileScreen(
                profileId = s.profileId,
                onBack = { router.openProfileList() },
            )
        }

        is Screen.TaskContext -> {
            TaskContextScreen(
                taskContextId = s.taskContextId,
                chatId = s.chatId,
                onBack = { router.openChat(it) },
            )
        }
    }
}
