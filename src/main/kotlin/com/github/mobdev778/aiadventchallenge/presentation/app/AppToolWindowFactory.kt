package com.github.mobdev778.aiadventchallenge.presentation.app

import com.github.mobdev778.aiadventchallenge.presentation.logsscreen.LogsFileScreen
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import org.jetbrains.jewel.bridge.addComposeTab

class AppToolWindowFactory : ToolWindowFactory {

    override fun shouldBeAvailable(project: Project) = true

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        PluginInitializer.ensureKoinStarted()

        toolWindow.addComposeTab("AI Chat", focusOnClickInside = true) {
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
