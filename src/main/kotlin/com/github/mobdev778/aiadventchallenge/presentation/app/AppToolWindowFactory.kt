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

class AppToolWindowFactory : ToolWindowFactory {

    override fun shouldBeAvailable(project: Project) = true

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        PluginInitializer.ensureKoinStarted()

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
