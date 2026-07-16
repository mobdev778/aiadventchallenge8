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
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtilCore
import kotlinx.coroutines.CoroutineScope

/**
 * Агент, генерирующий краткое техническое описание Kotlin-файла из проекта.
 *
 * `DraftDocumentFileAgent` обрабатывает запросы, начинающиеся с `/draft-document-file`,
 * извлекает путь к файлу, считывает его содержимое через файловую систему IntelliJ и
 * отправляет в языковую модель системный запрос на составление описания класса.
 * Результат помечается как промежуточный ответ ([AgentResponse.intermediate]).
 *
 * Использует [ProjectContainer] для получения текущего проекта и [LocalFileSystem] для
 * поиска виртуального файла. Взаимодействует с [ChatClient] через базовый класс [BaseAgent].
 *
 * @constructor Создаёт агента с указанным идентификатором и зависимостями.
 * @param id Уникальный идентификатор агента.
 * @param invariantRegistry Реестр инвариантов, используемый для валидации ответов.
 * @param settingsRepository Репозиторий настроек, передаваемый в базовый класс.
 * @param chatClient Фасад для взаимодействия с Chat API.
 * @param mcpServerInteractor Интерактор MCP-серверов для вызова инструментов.
 * @param scope Корутин-скоуп для выполнения асинхронных операций.
 * @param projectContainer Контейнер текущего проекта IntelliJ IDEA, необходим для доступа к VFS.
 */
class DraftDocumentFileAgent(
    id: String,
    invariantRegistry: InvariantRegistry,
    settingsRepository: SettingsRepository,
    chatClient: ChatClient,
    mcpServerInteractor: McpServerInteractor,
    scope: CoroutineScope,
    private val projectContainer: ProjectContainer,
) : BaseAgent(id, invariantRegistry, settingsRepository, chatClient, mcpServerInteractor, scope) {

    /**
     * Обрабатывает запрос на составление описания файла.
     *
     * Извлекает путь к файлу из текста запроса (после префикса `/draft-document-file`),
     * считывает содержимое файла через [LocalFileSystem] и [VfsUtilCore.loadText].
     * Затем формирует системный промпт для языковой модели и отправляет запрос через
     * [sendMessage], передавая содержимое файла как запрос пользователя.
     *
     * @param orchestrator Оркестратор агентов, не используется в текущей реализации.
     * @param context Контекст агента, содержит историю сообщений, инструменты и инварианты.
     * @param request Исходный запрос пользователя, должен содержать полный путь к файлу.
     * @return Промежуточный ответ ([AgentResponse]), содержащий сгенерированное описание.
     */
    override suspend fun handle(
        orchestrator: AgentOrchestrator,
        context: AgentContext,
        request: AgentRequest
    ): AgentResponse {
        val filePath = request.query.substringAfter("/draft-document-file ")
        println("!!! DraftDocumentFileAgent. request: $filePath")

        val virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath)
        val fileContent = virtualFile?.let { VfsUtilCore.loadText(it) } ?: ""

        val systemPrompt = "Ты — опытный Kotlin-архитектор. Твоя задача — проанализировать переданный класс и " +
                "составить его краткое техническое описание.\n" +
                "Ответ должен строго состоять из 4–5 предложений и включать:\" +" +
                "1. Основное назначение класса (что это за компонент).\n" +
                "2. Его главную зону ответственности в архитектуре.\n" +
                "3. Ключевые функции или бизнес-логику, которую он выполняет.\n" +
                "4. Основные зависимости или сущности, с которыми он взаимодействует.\n" +
                "\n" +
                "Пиши кратко, технически грамотно, без приветствий, вводных слов и форматирования кода в ответе.\n" +
                "Используй понятную русскоязычную терминологию."

        return sendMessage(context, systemPrompt, request.copy(query = fileContent)).copy(intermediate = true)
    }

    /**
     * Возвращает пустую строку, так как агент не имеет дополнительных правил поведения.
     *
     * @return Пустая строка.
     */
    override suspend fun getAgentRules(): String = ""
}
