package com.github.mobdev778.aiadventchallenge.presentation.app

import com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen.ChatListScreenStateHolder
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenStateHolder
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreenStateHolder
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import org.jetbrains.jewel.bridge.addComposeTab
import org.koin.java.KoinJavaComponent.inject

class AppToolWindowFactory : ToolWindowFactory {

    override fun shouldBeAvailable(project: Project) = true

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        PluginInitializer.ensureKoinStarted()

        val chatListScreenStateHolder by inject<ChatListScreenStateHolder>(ChatListScreenStateHolder::class.java)
        val chatScreenStateHolder by inject<ChatScreenStateHolder>(ChatScreenStateHolder::class.java)
        val settingsScreenStateHolder by inject<SettingsScreenStateHolder>(SettingsScreenStateHolder::class.java)

        toolWindow.addComposeTab("AI Chat", focusOnClickInside = true) {
            val router = rememberAppRouter()

            AppRouterContent(
                router = router,
                chatListScreenStateHolder = chatListScreenStateHolder,
                chatScreenStateHolderFactory = { _ -> chatScreenStateHolder },
                settingsScreenStateHolder = settingsScreenStateHolder,
            )
        }
    }
}
