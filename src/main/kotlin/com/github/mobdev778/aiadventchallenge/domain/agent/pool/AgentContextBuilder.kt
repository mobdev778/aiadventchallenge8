package com.github.mobdev778.aiadventchallenge.domain.agent.pool

import com.github.mobdev778.aiadventchallenge.data.profile.repository.ProfileRepository
import com.github.mobdev778.aiadventchallenge.data.taskcontext.repository.TaskContextRepository
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentContext
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentRequest
import com.github.mobdev778.aiadventchallenge.domain.chat.ObserveWindowMessagesUseCase
import com.github.mobdev778.aiadventchallenge.domain.invariant.InvariantRegistry
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single

@Single
class AgentContextBuilder(
    private val profileRepository: ProfileRepository,
    private val taskContextRepository: TaskContextRepository,
    private val observeWindowMessagesUseCase: ObserveWindowMessagesUseCase,
    private val invariantRegistry: InvariantRegistry,
) {

    suspend fun build(request: AgentRequest): AgentContext {
        val profile = profileRepository.getSelectedProfile()
        val taskContext = when {
            request.taskContextId != null -> taskContextRepository.getTaskContext(request.taskContextId)
            else -> null
        }
        val windowMessages = observeWindowMessagesUseCase.invoke(request.chatId).first()
        return AgentContext(
            profile = profile,
            taskContext = taskContext,
            windowMessages = windowMessages,
            invariants = invariantRegistry.getInvariants(),
        )
    }
}