package com.github.mobdev778.aiadventchallenge.domain.agent.agents.document

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.agent.AgentOrchestrator
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.BaseAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentContext
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentRequest
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentResponse
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.invariant.InvariantRegistry
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.McpServerInteractor
import com.github.mobdev778.aiadventchallenge.presentation.app.ProjectContainer
import com.intellij.openapi.application.readAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VfsUtilCore
import kotlinx.coroutines.CoroutineScope

/**
 * Агент, отвечающий за автоматическое документирование исходного кода на Kotlin
 * с помощью языковой модели.
 *
 * Ожидает, что в метаинформации запроса ([AgentRequest.meta]) будет передан узел графа
 * ([KtGraphNode]), содержащий путь к файлу и множество связанных узлов. На основе
 * предоставленного контекста (описаний дочерних узлов) и содержимого файла агент
 * формирует системный промпт, запрашивает у LLM готовый файл с KDoc-комментариями,
 * и при успешном ответе заменяет содержимое файла в IDE.
 *
 * @param id Уникальный идентификатор агента.
 * @param invariantRegistry Реестр инвариантов, используемых при валидации ответов.
 * @param settingsRepository Хранилище настроек приложения.
 * @param chatClient Клиент для взаимодействия с Chat API.
 * @param mcpServerInteractor Интерактор внешних инструментов, доступных агенту.
 * @param scope Контекст корутины, в котором работает агент.
 * @param projectContainer Контейнер текущего проекта IntelliJ IDEA для доступа к PSI и VFS.
 */
@Suppress("LongParameterList")
class DocumentFileAgent(
    id: String,
    invariantRegistry: InvariantRegistry,
    settingsRepository: SettingsRepository,
    chatClient: ChatClient,
    mcpServerInteractor: McpServerInteractor,
    scope: CoroutineScope,
    private val projectContainer: ProjectContainer,
) : BaseAgent(id, invariantRegistry, settingsRepository, chatClient, mcpServerInteractor, scope) {

    /**
     * Возвращает пустые дополнительные правила, так как данный агент не требует
     * специальных ограничений при генерации ответа.
     *
     * @return пустая строка
     */
    override suspend fun getAgentRules(): String = ""

    /**
     * Основная логика обработки запроса на документирование файла.
     *
     * Из метаинформации запроса извлекается узел [KtGraphNode], по которому строится
     * системный промпт с описаниями связанных классов. Затем содержимое файла читается
     * через VFS, отправляется в модель, и полученный задокументированный код при необходимости
     * записывается обратно в файл.
     *
     * @param orchestrator Оркестратор агентов, через который при необходимости можно
     *                     инициировать асинхронные вызовы.
     * @param context Контекст задачи, включающий профиль, историю и доступные инструменты.
     * @param request Запрос пользователя, в котором [AgentRequest.meta] должен содержать
     *                экземпляр [KtGraphNode].
     * @return Промежуточный ответ ([AgentResponse.intermediate] = true) с сообщением
     *         модели или пустой ответ в случае невозможности прочитать документ.
     */
    override suspend fun handle(
        orchestrator: AgentOrchestrator,
        context: AgentContext,
        request: AgentRequest
    ): AgentResponse {
        println("!!! DocumentFileAgent")

        val node = request.meta as KtGraphNode

        val children = node.links
            .map { "Класс: ${it.item.name}, описание: [${it.description}" }
            .toList()

        val systemPrompt = getSystemPrompt(children)

        val virtualFile = node.item.virtualFile
        val document = readAction {
            FileDocumentManager.getInstance().getDocument(virtualFile)
        }
        if (document != null) {
            println("!!! document: $document")

            val fileContent = readAction {
                VfsUtilCore.loadText(virtualFile)
            }

            val response = sendMessage(context, systemPrompt, request.copy(query = fileContent))
            val project = projectContainer.project
            var documentedContent = response.message
            if (documentedContent.startsWith("```kotlin")) {
                documentedContent = documentedContent.substringAfter("```kotlin")
            }
            if (documentedContent.endsWith("```")) {
                documentedContent = documentedContent.substringBeforeLast("```")
            }

            if (documentedContent.length > fileContent.length) {
                WriteCommandAction.runWriteCommandAction(
                    project,
                    "Replace File Content",
                    null,
                    { document.setText(documentedContent) }
                )
            }
            return response.copy(intermediate = true)
        } else {
            println("!!! document: null !!!")
            return AgentResponse(
                agent = id,
                intermediate = true,
                request = request,
                toolMessages = emptyList(),
                message = "",
                requestTokens = 0,
                responseTokens = 0,
                taskContext = null,
            )
        }
    }

    private fun getSystemPrompt(children: List<String>): String {
        return """
            Ты — инструмент статического анализа и документирования кода на Kotlin. Твоя единственная задача —
            проанализировать переданный в user-промпте код и добавить к нему профессиональные комментарии
            в формате KDoc на русском языке.

            Известно, что файл текущий файл, который нужно документировать, содержит ссылки на другие файлы:

            Для точного понимания логики и взаимосвязей используй следующий контекст файла:
            $children

            Правила написания KDoc:
            1. Документируй главный класс/интерфейс, его назначение и роль в архитектуре с учетом предоставленного
            контекста дочерних файлов.
            2. Документируй все публичные и защищенные функции. Используй теги `@param` и `@return` на русском языке.
            3. Сохраняй оригинальную логику, форматирование и структуру исходного кода. Не проводи рефакторинг.

            Ограничения на ответ:
            - Верни ТОЛЬКО полный, валидный Kotlin-код с добавленными KDoc-комментариями.
            - НЕ удаляй названия пакетов (package строку) и import-ы (import строки) из кода.
            - НЕ оборачивай код в markdown-блоки вида ```kotlin ... ```.
            - НЕ пиши никаких приветствий, пояснений и вводных слов.
        """.trimIndent()
    }
}
