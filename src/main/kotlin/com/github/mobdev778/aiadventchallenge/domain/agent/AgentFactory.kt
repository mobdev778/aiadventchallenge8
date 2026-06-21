package com.github.mobdev778.aiadventchallenge.domain.agent

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.Agent
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.ChatAssistantAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.ExecutorAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.PlannerAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.SummarizerAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.ValidatorAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentType
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.invariant.InvariantRegistry
import com.jetbrains.rd.util.UUID
import org.koin.core.annotation.Single

@Single
class AgentFactory(
    private val invariantRegistry: InvariantRegistry,
    private val settingsRepository: SettingsRepository,
    private val chatClient: ChatClient,
) {

    fun create(type: AgentType): Agent {
        val id = UUID.randomUUID().toString().replace("-", "")
        return when (type) {
            AgentType.ChatAssistant -> {
                ChatAssistantAgent(id, invariantRegistry, settingsRepository, chatClient)
            }
            AgentType.Planner -> {
                PlannerAgent(id, invariantRegistry, settingsRepository, chatClient)
            }
            AgentType.Executor -> {
                ExecutorAgent(id,invariantRegistry, settingsRepository, chatClient)
            }
            AgentType.Validator -> {
                ValidatorAgent(id, invariantRegistry, settingsRepository, chatClient)
            }
            AgentType.Summarizer -> {
                SummarizerAgent(id, invariantRegistry, settingsRepository, chatClient)
            }
        }
    }
}