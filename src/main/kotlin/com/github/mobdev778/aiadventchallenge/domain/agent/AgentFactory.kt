package com.github.mobdev778.aiadventchallenge.domain.agent

import com.github.mobdev778.aiadventchallenge.data.profile.repository.ProfileRepository
import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.Agent
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.ChatAssistantAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.document.DocumentProjectAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.ExecutorAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.PlannerAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.SummarizerAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.ValidatorAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.document.DocumentFileAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.document.DraftDocumentFileAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.help.HelpAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentType
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.invariant.InvariantRegistry
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.McpServerInteractor
import com.github.mobdev778.aiadventchallenge.presentation.app.ProjectContainer
import com.intellij.openapi.project.Project
import com.jetbrains.rd.util.UUID
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Single

/**
 * Фабрика для создания экземпляров агентов системы.
 *
 * Инкапсулирует логику конфигурирования и предоставления общих зависимостей,
 * таких как [ChatClient], [InvariantRegistry], [SettingsRepository],
 * [McpServerInteractor], [ProfileRepository], [ProjectContainer] и [CoroutineScope].
 * Каждому агенту присваивается уникальный идентификатор, основанный на случайном UUID.
 *
 * Используется Koin-аннотацией [Single] для обеспечения единственного экземпляра
 * фабрики в рамках приложения.
 */
@Single
class AgentFactory(
    private val invariantRegistry: InvariantRegistry,
    private val settingsRepository: SettingsRepository,
    private val chatClient: ChatClient,
    private val mcpServerInteractor: McpServerInteractor,
    private val scope: CoroutineScope,
    private val profileRepository: ProfileRepository,
    private val projectContainer: ProjectContainer,
) {

    /**
     * Создаёт и возвращает агента заданного типа.
     *
     * Каждый вызов генерирует новый экземпляр агента с уникальным идентификатором.
     * Конструирование агентов выполняется в соответствии с их зарегистрированными
     * зависимостями; для агентов, работающих с проектом, используется [projectContainer].
     *
     * @param type Тип создаваемого агента, определяющий его роль и поведение в системе.
     * @return Готовый к использованию экземпляр [Agent].
     */
    fun create(type: AgentType): Agent {
        val id = UUID.randomUUID().toString().replace("-", "")
        return when (type) {
            AgentType.ChatAssistant -> {
                ChatAssistantAgent(
                    id, invariantRegistry, settingsRepository, chatClient, mcpServerInteractor, scope,
                    profileRepository
                )
            }

            AgentType.Planner -> {
                PlannerAgent(id, invariantRegistry, settingsRepository, chatClient, mcpServerInteractor, scope)
            }
            AgentType.Executor -> {
                ExecutorAgent(id,invariantRegistry, settingsRepository, chatClient, mcpServerInteractor, scope)
            }
            AgentType.Validator -> {
                ValidatorAgent(id, invariantRegistry, settingsRepository, chatClient, mcpServerInteractor, scope)
            }
            AgentType.Summarizer -> {
                SummarizerAgent(id, invariantRegistry, settingsRepository, chatClient, mcpServerInteractor, scope)
            }

            AgentType.DocumentProject -> {
                DocumentProjectAgent(
                    id,
                    invariantRegistry,
                    settingsRepository,
                    chatClient,
                    mcpServerInteractor,
                    scope,
                    projectContainer
                )
            }

            AgentType.DraftDocumentFile -> {
                DraftDocumentFileAgent(
                    id,
                    invariantRegistry,
                    settingsRepository,
                    chatClient,
                    mcpServerInteractor,
                    scope,
                    projectContainer
                )
            }

            AgentType.DocumentFile -> {
                DocumentFileAgent(
                    id,
                    invariantRegistry,
                    settingsRepository,
                    chatClient,
                    mcpServerInteractor,
                    scope,
                    projectContainer
                )
            }

            AgentType.Help -> {
                HelpAgent(
                    id,
                    invariantRegistry,
                    settingsRepository,
                    chatClient,
                    mcpServerInteractor,
                    scope,
                    projectContainer
                )
            }
        }
    }
}
