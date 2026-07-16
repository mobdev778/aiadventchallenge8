package com.github.mobdev778.aiadventchallenge.presentation.app

import androidx.compose.runtime.LaunchedEffect
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.MyMcpServerInteractor
import com.github.mobdev778.aiadventchallenge.presentation.logsscreen.LogsFileScreen
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import kotlinx.coroutines.flow.first
import org.jetbrains.jewel.bridge.addComposeTab
import org.koin.java.KoinJavaComponent.inject

/**
 * Фабрика для создания окна инструментов "AI Adventure Challenge".
 *
 * Реализует [ToolWindowFactory], регистрируя окно инструментов с двумя вкладками:
 * - "AI Chat" — основное рабочее пространство с маршрутизацией и управлением MCP-серверами.
 * - "Logs" — просмотр логов работы MCP-серверов.
 *
 * При первом открытии окна гарантирует инициализацию контейнера внедрения зависимостей Koin
 * (через [PluginInitializer.ensureKoinStarted]), после чего инъецирует необходимые сервисы
 * и выполняет автоматический запуск серверов, у которых установлен флаг `launchAtStartup`.
 */
class AppToolWindowFactory : ToolWindowFactory {

    /**
     * Определяет, должно ли быть доступно окно инструментов для указанного проекта.
     *
     * Окно доступно всегда, независимо от состояния проекта.
     *
     * @param project текущий проект IntelliJ.
     * @return `true` — окно всегда доступно.
     */
    override fun shouldBeAvailable(project: Project) = true

    /**
     * Создаёт содержимое окна инструментов.
     *
     * Выполняет следующие действия:
     * 1. Гарантирует старт Koin (ди) — [PluginInitializer.ensureKoinStarted].
     * 2. Инъецирует [ProjectContainer] и [MyMcpServerInteractor].
     * 3. Добавляет вкладку "AI Chat", внутри которой:
     *    - При первой композиции считывает текущий список состояний серверов и запускает те,
     *      у которых [MyMcpServerState.launchAtStartup] равен `true`.
     *    - Связывает текущий проект с контейнером [ProjectContainer].
     *    - Создаёт и отображает маршрутизатор [AppRouterContent].
     * 4. Добавляет вкладку "Logs" с компонентом [LogsFileScreen] для просмотра логов.
     *
     * @param project проект, в контексте которого открывается окно.
     * @param toolWindow экземпляр окна инструментов, в который добавляются вкладки.
     */
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        PluginInitializer.ensureKoinStarted()

        val projectContainer by inject<ProjectContainer>(ProjectContainer::class.java)
        val myMcpServerInteractor by inject<MyMcpServerInteractor>(MyMcpServerInteractor::class.java)

        toolWindow.addComposeTab("AI Chat", focusOnClickInside = true) {
            LaunchedEffect(Unit) {
                val servers = myMcpServerInteractor.serverStatesFlow.first()
                servers.forEach { server ->
                    if (server.launchAtStartup) {
                        myMcpServerInteractor.start(server.name)
                    }
                }
            }

            projectContainer.project = toolWindow.project

            val router = rememberAppRouter()
            AppRouterContent(
                router = router,
            )
        }

        toolWindow.addComposeTab("Logs", focusOnClickInside = true) {
            LogsFileScreen()
        }
    }
}
