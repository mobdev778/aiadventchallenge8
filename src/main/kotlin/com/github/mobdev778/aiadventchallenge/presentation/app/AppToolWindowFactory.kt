package com.github.mobdev778.aiadventchallenge.presentation.app

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.ChatClient
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreen
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenStateHolder
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import org.jetbrains.jewel.bridge.addComposeTab
import org.koin.java.KoinJavaComponent.inject

class AppToolWindowFactory : ToolWindowFactory {

    override fun shouldBeAvailable(project: Project) = true

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // Ensure Koin is started even if the toolwindow is created before postStartupActivity.
        PluginInitializer.ensureKoinStarted()

        val chatClient: ChatClient by inject(ChatClient::class.java)

        toolWindow.addComposeTab("AiAgent", focusOnClickInside = true) {
            LaunchedEffect(Unit) {
                // initial data loading
            }

            val stateHolder = remember(chatClient) { ChatScreenStateHolder(chatClient = chatClient) }
            ChatScreen(stateHolder = stateHolder)
        }
    }
}
