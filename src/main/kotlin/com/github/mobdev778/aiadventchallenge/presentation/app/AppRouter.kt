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

/**
 * Запечатанный интерфейс, определяющий все экраны (маршруты) приложения.
 *
 * Используется в сочетании с [AppRouter] для декларативного описания текущего места
 * назначения. Каждый из вложенных классов/объектов представляет конкретный экран и
 * содержит необходимые для его отображения параметры (например, идентификаторы чата,
 * профиля, сервера MCP, документа RAG и т.д.).
 *
 * Связь с конкретными экранами:
 * - [ChatList], [Chat] используют [ChatListScreen], [ChatScreen]
 * - [Settings] — [SettingsScreen]
 * - [ProfileList], [AddProfile], [EditProfile] — [ProfileListScreen], [AddProfileScreen], [EditProfileScreen]
 * - [RagDocumentList], [RagConfig], [AddRagDocument], [ViewRagDocument], [AddingRagDocument] — соответствующие
 *   экраны RAG-системы
 * - [McpServerList], [MyMcpServer], [AddMcpServer], [McpInfo] — экраны управления MCP-серверами
 * - [TaskContext] — [TaskContextScreen]
 */
@Immutable
sealed interface Screen {
    /** Экран списка чатов. */
    data object ChatList : Screen
    /** Экран конкретного чата. */
    data class Chat(val chatId: UUID) : Screen
    /** Главный экран настроек. */
    data object Settings : Screen
    /** Экран списка профилей. */
    data object ProfileList : Screen
    /** Экран добавления нового профиля. */
    data object AddProfile : Screen
    /** Экран редактирования существующего профиля. */
    data class EditProfile(val profileId: UUID) : Screen
    /** Экран списка документов RAG. */
    data object RagDocumentList : Screen
    /** Экран конфигурации параметров RAG. */
    data object RagConfig : Screen
    /** Экран добавления нового RAG-документа. */
    data object AddRagDocument : Screen
    /** Экран просмотра содержимого конкретного RAG-документа. */
    data class ViewRagDocument(val document: RagDocumentListItem) : Screen
    /** Экран подтверждения и запуска процесса добавления RAG-документа. */
    data class AddingRagDocument(
        val source: String,
        val title: String,
        val chunkingStrategy: AddRagDocumentScreenState.ChunkingStrategy,
    ) : Screen
    /** Экран списка MCP-серверов. */
    data object McpServerList : Screen
    /** Экран «Мои MCP-серверы». */
    data object MyMcpServer : Screen
    /** Экран добавления нового MCP-сервера. */
    data object AddMcpServer : Screen
    /** Экран с подробной информацией об MCP-сервере. */
    data class McpInfo(val serverId: UUID) : Screen
    /** Экран контекста задачи, отображающий детали задачи. */
    data class TaskContext(val taskContextId: UUID, val chatId: UUID) : Screen
}

/**
 * Маршрутизатор экранов приложения.
 *
 * Управляет текущим состоянием навигации, храня текущий экран в виде [Screen].
 * Предоставляет потокобезопасное (благодаря Compose `mutableStateOf`) чтение и запись
 * через свойство [screen]. Все методы-переходы (open*) изменяют текущее состояние,
 * что приводит к перекомпозиции дерева, использующего [AppRouterContent].
 *
 * Экземпляр обычно создаётся через [rememberAppRouter] и передаётся как параметр
 * в корневую композицию.
 *
 * @param initial Начальный экран, на котором откроется приложение. По умолчанию [Screen.ChatList].
 */
@Suppress("TooManyFunctions")
class AppRouter(initial: Screen = Screen.ChatList) {
    /**
     * Текущий экран, отображаемый в UI.
     *
     * Поддерживается Compose-состоянием, поэтому его изменение автоматически вызывает
     * перекомпозицию компонентов, читающих это значение.
     */
    var screen: Screen by mutableStateOf(initial)
        private set

    /**
     * Переход на экран списка чатов.
     */
    fun openChatList() {
        screen = Screen.ChatList
    }

    /**
     * Открытие конкретного чата.
     *
     * @param chatId Уникальный идентификатор чата.
     */
    fun openChat(chatId: UUID) {
        screen = Screen.Chat(chatId)
    }

    /**
     * Переход на основной экран настроек.
     */
    fun openSettings() {
        screen = Screen.Settings
    }

    /**
     * Переход к списку профилей.
     */
    fun openProfileList() {
        screen = Screen.ProfileList
    }

    /**
     * Открытие экрана добавления нового профиля.
     */
    fun openAddProfile() {
        screen = Screen.AddProfile
    }

    /**
     * Открытие экрана редактирования существующего профиля.
     *
     * @param profileId Уникальный идентификатор профиля.
     */
    fun openEditProfile(profileId: UUID) {
        screen = Screen.EditProfile(profileId)
    }

    /**
     * Переход к списку документов RAG.
     */
    fun openRagDocumentList() {
        screen = Screen.RagDocumentList
    }

    /**
     * Открытие экрана конфигурации RAG.
     */
    fun openRagConfig() {
        screen = Screen.RagConfig
    }

    /**
     * Открытие экрана добавления нового документа RAG.
     */
    fun openAddRagDocument() {
        screen = Screen.AddRagDocument
    }

    /**
     * Открытие экрана просмотра содержимого конкретного документа RAG.
     *
     * @param document Модель элемента списка документов RAG (содержит id, источник, заголовок, количество чанков).
     */
    fun openViewRagDocument(document: RagDocumentListItem) {
        screen = Screen.ViewRagDocument(document)
    }

    /**
     * Открытие экрана подтверждения добавления документа RAG с заполненными параметрами.
     *
     * @param source Источник документа (URL, путь к файлу и т.п.).
     * @param title Заголовок документа.
     * @param chunkingStrategy Выбранная стратегия разбиения на чанки.
     */
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

    /**
     * Переход к списку MCP-серверов.
     */
    fun openMcpServerList() {
        screen = Screen.McpServerList
    }

    /**
     * Переход на экран «Мои MCP-серверы».
     */
    fun openMyMcpServer() {
        screen = Screen.MyMcpServer
    }

    /**
     * Открытие экрана добавления нового MCP-сервера.
     */
    fun openAddMcpServer() {
        screen = Screen.AddMcpServer
    }

    /**
     * Открытие экрана с подробной информацией об MCP-сервере.
     *
     * @param serverId Уникальный идентификатор сервера.
     */
    fun openMcpInfo(serverId: UUID) {
        screen = Screen.McpInfo(serverId)
    }

    /**
     * Открытие экрана контекста задачи.
     *
     * @param taskContextId Идентификатор контекста задачи.
     * @param chatId Идентификатор чата, связанного с задачей.
     */
    fun openTaskContext(taskContextId: UUID, chatId: UUID) {
        screen = Screen.TaskContext(taskContextId = taskContextId, chatId = chatId)
    }
}

/**
 * Создаёт и запоминает экземпляр [AppRouter] на всё время жизни композиции.
 *
 * Используйте эту функцию на верхнем уровне вашей Compose-иерархии для инициализации
 * навигатора с заданным начальным экраном [initial]. Возвращаемый объект передаётся
 * в [AppRouterContent] для отображения контента.
 *
 * @param initial Начальный экран, на котором откроется приложение (по умолчанию [Screen.ChatList]).
 * @return Запомненный экземпляр [AppRouter].
 */
@Composable
fun rememberAppRouter(initial: Screen = Screen.ChatList): AppRouter = remember { AppRouter(initial) }

/**
 * Корневая Composable-функция, отображающая контент текущего экрана на основе состояния [AppRouter].
 *
 * Анализирует текущий экран из [router.screen] и делегирует отрисовку соответствующим
 * Composable-функциям экранов (например, [ChatListScreen], [ChatScreen], [SettingsScreen]).
 * Для вложенных групп экранов (профили, RAG, MCP, контекст задачи) передаёт управление
 * внутренним приватным функциям ([SecondaryScreenContent]), которые осуществляют
 * дальнейшую диспетчеризацию.
 *
 * Все колбэки навигации, передаваемые экранам, замыкаются на вызовах методов [AppRouter],
 * таким образом создавая замкнутую систему навигации.
 *
 * @param router Экземпляр маршрутизатора, предоставляющий текущий экран и методы переходов.
 */
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

        else -> SecondaryScreenContent(router = router, screen = s)
    }
}

@Composable
private fun SecondaryScreenContent(
    router: AppRouter,
    screen: Screen,
) {
    when (screen) {
        is Screen.ProfileList, is Screen.AddProfile, is Screen.EditProfile ->
            ProfileScreensContent(router = router, screen = screen)

        is Screen.RagDocumentList, is Screen.RagConfig, is Screen.AddRagDocument,
        is Screen.ViewRagDocument, is Screen.AddingRagDocument ->
            RagScreensContent(router = router, screen = screen)

        is Screen.McpServerList, is Screen.MyMcpServer, is Screen.AddMcpServer,
        is Screen.McpInfo ->
            McpScreensContent(router = router, screen = screen)

        is Screen.TaskContext ->
            TaskContextScreenContent(router = router, screen = screen)

        else -> {} // ChatList, Chat, Settings handled by parent
    }
}

@Composable
private fun ProfileScreensContent(router: AppRouter, screen: Screen) {
    when (screen) {
        is Screen.ProfileList -> {
            ProfileListScreen(
                onBack = { router.openSettings() },
                onOpenAddProfile = { router.openAddProfile() },
                onOpenEditProfile = { router.openEditProfile(it) },
            )
        }
        is Screen.AddProfile -> {
            AddProfileScreen(onBack = { router.openProfileList() })
        }
        is Screen.EditProfile -> {
            EditProfileScreen(
                profileId = screen.profileId,
                onBack = { router.openProfileList() },
            )
        }
        else -> {}
    }
}

@Composable
private fun RagScreensContent(router: AppRouter, screen: Screen) {
    when (screen) {
        is Screen.RagDocumentList -> {
            RagDocumentListScreen(
                onBack = { router.openSettings() },
                onOpenAddDocument = { router.openAddRagDocument() },
                onOpenDocument = { router.openViewRagDocument(it) },
                onOpenRagConfig = { router.openRagConfig() },
            )
        }
        is Screen.RagConfig -> {
            RagConfigScreen(onBack = { router.openRagDocumentList() })
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
                document = screen.document,
                onBack = { router.openRagDocumentList() },
            )
        }
        is Screen.AddingRagDocument -> {
            AddingRagDocumentScreen(
                source = screen.source,
                title = screen.title,
                chunkingStrategy = screen.chunkingStrategy,
                onBackToAddDocument = { router.openAddRagDocument() },
                onBackToDocumentList = { router.openRagDocumentList() },
            )
        }
        else -> {}
    }
}

@Composable
private fun McpScreensContent(router: AppRouter, screen: Screen) {
    when (screen) {
        is Screen.McpServerList -> {
            McpServerListScreen(
                onBack = { router.openSettings() },
                onOpenAddServer = { router.openAddMcpServer() },
                onOpenServerInfo = { router.openMcpInfo(it) },
            )
        }
        is Screen.MyMcpServer -> {
            MyMcpServerScreen(onBack = { router.openSettings() })
        }
        is Screen.AddMcpServer -> {
            AddMcpServerScreen(onBack = { router.openMcpServerList() })
        }
        is Screen.McpInfo -> {
            McpInfoScreen(
                serverId = screen.serverId,
                onBack = { router.openMcpServerList() },
            )
        }
        else -> {}
    }
}

@Composable
private fun TaskContextScreenContent(router: AppRouter, screen: Screen) {
    val taskScreen = screen as Screen.TaskContext
    TaskContextScreen(
        taskContextId = taskScreen.taskContextId,
        chatId = taskScreen.chatId,
        onBack = { router.openChat(it) },
    )
}
