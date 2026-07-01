package com.github.mobdev778.aiadventchallenge.presentation.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.mobdev778.aiadventchallenge.presentation.addmcpserverscreen.AddMcpServerScreen
import com.github.mobdev778.aiadventchallenge.presentation.addprofilescreen.AddProfileScreen
import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.AddRagDocumentScreen
import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model.AddRagDocumentScreenState
import com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen.AddingRagDocumentScreen
import com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen.ChatListScreen
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreen
import com.github.mobdev778.aiadventchallenge.presentation.editprofilescreen.EditProfileScreen
import com.github.mobdev778.aiadventchallenge.presentation.mcpinfoscreen.McpInfoScreen
import com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen.McpServerListScreen
import com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.MyMcpServerScreen
import com.github.mobdev778.aiadventchallenge.presentation.profilelistscreen.ProfileListScreen
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen.RagConfigScreen
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.RagDocumentListScreen
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem
import com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen.ViewRagDocumentScreen
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
    data object RagDocumentList : Screen
    data object RagConfig : Screen
    data object AddRagDocument : Screen
    data class ViewRagDocument(val document: RagDocumentListItem) : Screen
    data class AddingRagDocument(
        val source: String,
        val title: String,
        val chunkingStrategy: AddRagDocumentScreenState.ChunkingStrategy,
    ) : Screen
    data object McpServerList : Screen
    data object MyMcpServer : Screen
    data object AddMcpServer : Screen
    data class McpInfo(val serverId: UUID) : Screen
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

    fun openRagDocumentList() {
        screen = Screen.RagDocumentList
    }

    fun openRagConfig() {
        screen = Screen.RagConfig
    }

    fun openAddRagDocument() {
        screen = Screen.AddRagDocument
    }

    fun openViewRagDocument(document: RagDocumentListItem) {
        screen = Screen.ViewRagDocument(document)
    }

    fun openAddingRagDocument(
        source: String,
        title: String,
        chunkingStrategy: AddRagDocumentScreenState.ChunkingStrategy,
    ) {
        screen = Screen.AddingRagDocument(
            source = source,
            title = title,
            chunkingStrategy = chunkingStrategy,
        )
    }

    fun openMcpServerList() {
        screen = Screen.McpServerList
    }

    fun openMyMcpServer() {
        screen = Screen.MyMcpServer
    }

    fun openAddMcpServer() {
        screen = Screen.AddMcpServer
    }

    fun openMcpInfo(serverId: UUID) {
        screen = Screen.McpInfo(serverId)
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
                onOpenProfiles = { router.openProfileList() },
                onOpenRag = { router.openRagDocumentList() },
                onOpenMcp = { router.openMcpServerList() },
                onOpenMyMcp = { router.openMyMcpServer() },
            )
        }

        is Screen.ProfileList -> {
            ProfileListScreen(
                onBack = { router.openSettings() },
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

        is Screen.RagDocumentList -> {
            RagDocumentListScreen(
                onBack = { router.openSettings() },
                onOpenAddDocument = { router.openAddRagDocument() },
                onOpenDocument = { router.openViewRagDocument(it) },
                onOpenRagConfig = { router.openRagConfig() },
            )
        }

        is Screen.RagConfig -> {
            RagConfigScreen(
                onBack = { router.openRagDocumentList() },
            )
        }

        is Screen.AddRagDocument -> {
            AddRagDocumentScreen(
                onBack = { router.openRagDocumentList() },
                onOpenAddingDocument = { source, title, chunkingStrategy ->
                    router.openAddingRagDocument(
                        source = source,
                        title = title,
                        chunkingStrategy = chunkingStrategy,
                    )
                },
            )
        }

        is Screen.ViewRagDocument -> {
            ViewRagDocumentScreen(
                document = s.document,
                onBack = { router.openRagDocumentList() },
            )
        }

        is Screen.AddingRagDocument -> {
            AddingRagDocumentScreen(
                source = s.source,
                title = s.title,
                chunkingStrategy = s.chunkingStrategy,
                onBackToAddDocument = { router.openAddRagDocument() },
                onBackToDocumentList = { router.openRagDocumentList() },
            )
        }

        is Screen.McpServerList -> {
            McpServerListScreen(
                onBack = { router.openSettings() },
                onOpenAddServer = { router.openAddMcpServer() },
                onOpenServerInfo = { router.openMcpInfo(it) },
            )
        }

        is Screen.MyMcpServer -> {
            MyMcpServerScreen(
                onBack = { router.openSettings() },
            )
        }

        is Screen.AddMcpServer -> {
            AddMcpServerScreen(
                onBack = { router.openMcpServerList() },
            )
        }

        is Screen.McpInfo -> {
            McpInfoScreen(
                serverId = s.serverId,
                onBack = { router.openMcpServerList() },
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
