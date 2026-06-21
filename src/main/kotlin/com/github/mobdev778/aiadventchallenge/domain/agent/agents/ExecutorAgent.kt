package com.github.mobdev778.aiadventchallenge.domain.agent.agents

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentContext
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentRequest
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentResponse
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.invariant.InvariantRegistry
import com.github.mobdev778.aiadventchallenge.domain.task.TaskState

class ExecutorAgent(
    id: String,
    invariantRegistry: InvariantRegistry,
    settingsRepository: SettingsRepository,
    chatClient: ChatClient,
) : BaseAgent(id, invariantRegistry, settingsRepository, chatClient) {

    override suspend fun getAgentRules(): String {
        return "Верни [VALIDATION], если считаешь, что реализация решения завершена и можно перейти к проверке. " +
                "Верни [PLANNING], если считаешь, что реализация получилась плохой/неправильной  (маловероятно, но все же) и нужно доуточнить требования"
    }

    override suspend fun handle(
        context: AgentContext,
        request: AgentRequest
    ): AgentResponse {
        val response = super.handle(context, request)

        val taskContext = response.taskContext
        return when {
            taskContext == null -> response
            response.message.contains("[VALIDATION]") -> {
                response.copy(
                    taskContext = taskContext.copy(
                        state = TaskState.Validation,
                        step = 1,
                        current = "Проверка решения"
                    )
                )
            }
            response.message.contains("[PLANNING]") -> {
                response.copy(
                    taskContext = taskContext.copy(
                        state = TaskState.Planning,
                        step = 1,
                        current = "Новое планирование"
                    )
                )
            }
            else -> response
        }
    }
}