package com.github.mobdev778.aiadventchallenge.domain.agent.agents

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentContext
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentRequest
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentResponse
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.invariant.InvariantRegistry
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.McpServerInteractor
import com.github.mobdev778.aiadventchallenge.domain.task.TaskState
import kotlinx.coroutines.CoroutineScope

class PlannerAgent(
    id: String,
    invariantRegistry: InvariantRegistry,
    settingsRepository: SettingsRepository,
    chatClient: ChatClient,
    mcpServerInteractor: McpServerInteractor,
    scope: CoroutineScope,
) : BaseAgent(id, invariantRegistry, settingsRepository, chatClient, mcpServerInteractor, scope) {

    override suspend fun getAgentRules(): String {
        return "Верни в конце ответа [EXECUTION] если этап планирования полностью завершен."
    }

    override suspend fun handle(
        context: AgentContext,
        request: AgentRequest
    ): AgentResponse {
        val response = super.handle(context, request)

        val taskContext = response.taskContext
        return when {
            taskContext == null -> response
            response.message.contains("[EXECUTION]") -> {
                response.copy(
                    taskContext = taskContext.copy(
                        state = TaskState.Execution,
                        step = 1,
                        current = "Новая задача"
                    )
                )
            }
            else -> response
        }
    }
}