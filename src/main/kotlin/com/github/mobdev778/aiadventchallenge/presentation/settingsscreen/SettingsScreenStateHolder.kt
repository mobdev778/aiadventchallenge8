package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen

import com.github.mobdev778.aiadventchallenge.domain.messageselection.MessageSelectionType
import com.github.mobdev778.aiadventchallenge.domain.settings.SettingsInteractor
import com.github.mobdev778.aiadventchallenge.domain.settings.model.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
class SettingsScreenStateHolder(
    private val settingsInteractor: SettingsInteractor,
    private val scope: CoroutineScope,
) {

    private val defaultDraft = SettingsDraft(
        messageSelectionType = MessageSelectionType.FullHistory,
        maxMessages = "",
        maxTokens = "",
        apiKey = "",
        baseUrl = "",
        baseModel = "",
    )

    /**
     * If repository already has settings, the first emission from observeSettings() must overwrite
     * this default draft.
     */
    private val draftFlow = MutableStateFlow(defaultDraft)

    private val savedSettingsFlow = settingsInteractor
        .observeSettings()
        .onEach { settings ->
            if (draftFlow.value === defaultDraft) {
                draftFlow.value = SettingsDraft.from(settings)
            }
        }

    val uiState: StateFlow<SettingsScreenState> = combine(
        savedSettingsFlow,
        draftFlow,
    ) { settings: AppSettings, draft: SettingsDraft ->
        SettingsScreenState(
            saved = settings,
            draft = draft,
        )
    }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsScreenState(
                saved = AppSettings(
                    messageSelectionType = MessageSelectionType.FullHistory,
                    maxMessages = 0,
                    maxTokens = 0,
                    apiKey = "",
                    baseUrl = "",
                    baseModel = "",
                ),
                draft = draftFlow.value,
            )
        )

    fun onEvent(event: SettingsScreenEvent) {
        when (event) {
            is SettingsScreenEvent.OnMessageSelectionTypeChanged -> draftFlow.update { it.copy(messageSelectionType = event.type) }
            is SettingsScreenEvent.OnLastNMessagesChanged -> draftFlow.update { it.copy(maxMessages = event.value) }
            is SettingsScreenEvent.OnMaxTokensChanged -> draftFlow.update { it.copy(maxTokens = event.value) }
            is SettingsScreenEvent.OnApiKeyChanged -> draftFlow.update { it.copy(apiKey = event.value) }
            is SettingsScreenEvent.OnBaseUrlChanged -> draftFlow.update { it.copy(baseUrl = event.value) }
            is SettingsScreenEvent.OnBaseModelChanged -> draftFlow.update { it.copy(baseModel = event.value) }
            SettingsScreenEvent.OnSaveClick -> save()
            SettingsScreenEvent.OnResetClick -> resetToSaved()
        }
    }

    private fun resetToSaved() {
        val saved = uiState.value.saved
        draftFlow.value = SettingsDraft.from(saved)
    }

    private fun save() {
        val saved = uiState.value.saved
        val draft = draftFlow.value

        val lastN = draft.maxMessages.trim().toIntOrNull() ?: saved.maxMessages
        val maxTokens = draft.maxTokens.trim().toIntOrNull() ?: saved.maxTokens

        scope.launch {
            settingsInteractor.update(
                AppSettings(
                    messageSelectionType = draft.messageSelectionType,
                    maxMessages = lastN,
                    maxTokens = maxTokens,
                    apiKey = draft.apiKey,
                    baseUrl = draft.baseUrl,
                    baseModel = draft.baseModel,
                )
            )
        }
    }
}

data class SettingsScreenState(
    val saved: AppSettings,
    val draft: SettingsDraft,
)

// --- Draft ---

data class SettingsDraft(
    val messageSelectionType: MessageSelectionType,
    val maxMessages: String,
    val maxTokens: String,
    val apiKey: String,
    val baseUrl: String,
    val baseModel: String,
) {
    companion object {
        fun from(settings: AppSettings): SettingsDraft {
            return SettingsDraft(
                messageSelectionType = settings.messageSelectionType,
                maxMessages = settings.maxMessages.toString(),
                maxTokens = settings.maxTokens.toString(),
                apiKey = settings.apiKey,
                baseUrl = settings.baseUrl,
                baseModel = settings.baseModel,
            )
        }
    }
}
