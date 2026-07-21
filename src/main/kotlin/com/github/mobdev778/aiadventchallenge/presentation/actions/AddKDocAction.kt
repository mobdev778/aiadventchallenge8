package com.github.mobdev778.aiadventchallenge.presentation.actions

import com.github.mobdev778.aiadventchallenge.domain.agent.AgentOrchestrator
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.document.KtGraphNode
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentRequest
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiFileSystemItem
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID
import kotlin.getValue

class AddKDocAction : AnAction("Добавить KDoc-документацию") {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(event: AnActionEvent) {
        val project = event.project
        val psiFile = event.getData(CommonDataKeys.PSI_FILE)

        // Проверяем, является ли файл Kotlin-файлом (по расширению или типу)
        val isKotlinFile = psiFile?.virtualFile?.extension == "kt"

        // Проверяем режим индексации
        val isDumb = project != null && DumbService.isDumb(project)

        // Теперь редактор (editor) не обязателен для дерева проектов
        event.presentation.isEnabledAndVisible = project != null
                && psiFile != null
                && isKotlinFile
                && !isDumb
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val psiFile: PsiFileSystemItem? = event.getData(CommonDataKeys.PSI_FILE)

        if (psiFile != null && !DumbService.isDumb(project)) {
            val agentOrchestrator by inject<AgentOrchestrator>(AgentOrchestrator::class.java)

            val meta = KtGraphNode(psiFile.virtualFile.path, psiFile)

            agentOrchestrator.startAgents()

            agentOrchestrator.asyncRequest(
                AgentRequest(
                    chatId = UUID.randomUUID(),
                    taskContextId = null,
                    parentMessageId = null,
                    time = System.currentTimeMillis(),
                    query = "/document-file ",
                    meta = meta,
                )
            )
        }
    }
}
