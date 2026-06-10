package com.github.mobdev778.aiadventchallenge.presentation.app

import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreen
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenStateHolder
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreen
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreenStateHolder
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import org.jetbrains.jewel.bridge.addComposeTab
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

class AppToolWindowFactory : ToolWindowFactory {

    override fun shouldBeAvailable(project: Project) = true

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        PluginInitializer.ensureKoinStarted()

        val chatScreenStateHolder by inject<ChatScreenStateHolder>(ChatScreenStateHolder::class.java)
        val settingsScreenStateHolder by inject<SettingsScreenStateHolder>(SettingsScreenStateHolder::class.java)

        toolWindow.addComposeTab("AI Chat", focusOnClickInside = true) {
            ChatScreen(stateHolder = chatScreenStateHolder)
        }

        toolWindow.addComposeTab("Settings", focusOnClickInside = true) {
            SettingsScreen(stateHolder = settingsScreenStateHolder)
        }
    }
}
