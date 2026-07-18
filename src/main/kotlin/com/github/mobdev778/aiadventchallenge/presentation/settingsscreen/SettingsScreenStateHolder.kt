package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.settings.SettingsInteractor
import com.github.mobdev778.aiadventchallenge.domain.settings.model.AppSettings
import com.github.mobdev778.aiadventchallenge.domain.settings.model.ContextManagementType
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.mapper.ContextManagementTypeMapper
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.model.SettingsScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

private const val STATE_FLOW_TIMEOUT_MS = 5000L

/**
 * State Holder экрана настроек.
 *
 * Отвечает за подготовку реактивного состояния экрана [SettingsScreenState],
 * объединяя сохранённые настройки из [SettingsInteractor] и редактируемый черновик.
 * Формирует поток команд [commands] для навигации и других действий, инициируемых UI.
 * Использует [SettingsRepository.default] для определения момента первого применения
 * настроек к черновику (если черновик ещё не изменялся пользователем, он инициализируется
 * из сохранённых значений).
 *
 * @property settingsInteractor интерактор для работы с настройками приложения.
 * @property scope корутинный скоуп, в котором происходит сборка состояний.
 */
@Single
class SettingsScreenStateHolder(
    private val settingsInteractor: SettingsInteractor,
    private val scope: CoroutineScope,
) {

    private val draftFlow = MutableStateFlow(SettingsRepository.default)

    private val savedSettingsFlow = settingsInteractor
        .observeSettings()
        .onEach { settings ->
            if (draftFlow.value === SettingsRepository.default) {
                draftFlow.value = settings
            }
        }

    /**
     * Поток состояния экрана, производный от сохранённых настроек ([savedSettingsFlow]) и
     * текущего черновика ([draftFlow]).
     * Содержит флаг [SettingsScreenState.actionEnabled], активирующий кнопку «Сохранить» при
     * отличии черновика от сохранённых данных, а также список типов управления контекстом,
     * отображённых в UI.
     *
     * @return [StateFlow] с текущим [SettingsScreenState].
     */
    val uiState: StateFlow<SettingsScreenState> = combine(
        savedSettingsFlow,
        draftFlow,
    ) { settings: AppSettings, draft: AppSettings ->
        SettingsScreenState(
            saved = settings,
            draft = draft,
            contextManagementTypes = ContextManagementType.entries.map {
                ContextManagementTypeMapper.map(it, it == draft.contextManagementType)
            },
            actionEnabled = settings != draft
        )
    }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(STATE_FLOW_TIMEOUT_MS),
            initialValue = SettingsScreenState(
                saved = AppSettings(
                    contextManagementType = ContextManagementType.None,
                    maxMessages = 0,
                    maxTokens = 0,
                    recursiveSummationMaxMessages = 0,
                    stickyFactsMaxMessages = 0,
                    apiKey = "",
                    baseUrl = "",
                    baseModel = "",
                ),
                draft = draftFlow.value,
                contextManagementTypes = ContextManagementType.entries.map {
                    ContextManagementTypeMapper.map(
                        contextManagementType = it,
                        selected = it == ContextManagementType.None
                    )
                },
                actionEnabled = false,
            )
        )

    /**
     * Поток однократных команд ([SettingsScreenCommand]), предназначенных для обработки
     * слоем UI (например, навигация). Каждая команда буферизируется (extraBufferCapacity=1),
     * чтобы дождаться готовности Compose-экрана к её приёму.
     */
    val commands = MutableSharedFlow<SettingsScreenCommand>(
        extraBufferCapacity = 1
    )

    /**
     * Обработчик событий от UI. В зависимости от типа события обновляет черновик,
     * инициирует сохранение или сброс, либо отправляет команду через [commands].
     *
     * @param event событие экрана настроек, соответствующее действию пользователя.
     */
    @Suppress("CyclomaticComplexMethod")
    fun onEvent(event: SettingsScreenEvent) {
        when (event) {
            is SettingsScreenEvent.OnBackClick -> {
                commands.tryEmit(SettingsScreenCommand.Back)
            }
            SettingsScreenEvent.OnOpenProfilesClick -> {
                commands.tryEmit(SettingsScreenCommand.OpenProfiles)
            }
            SettingsScreenEvent.OnOpenRagClick -> {
                commands.tryEmit(SettingsScreenCommand.OpenRag)
            }
            SettingsScreenEvent.OnOpenMcpClick -> {
                commands.tryEmit(SettingsScreenCommand.OpenMcp)
            }
            SettingsScreenEvent.OnOpenMyMcpClick -> {
                commands.tryEmit(SettingsScreenCommand.OpenMyMcp)
            }
            is SettingsScreenEvent.OnContextManagementTypeChanged -> {
                updateContextManagementType(event.type)
            }
            is SettingsScreenEvent.OnMaxMessagesChanged -> {
                updateMaxMessages(event.value)
            }
            is SettingsScreenEvent.OnMaxTokensChanged -> {
                updateMaxTokens(event.value)
            }
            is SettingsScreenEvent.OnRecursiveSummationMaxMessagesChanged -> {
                updateRecursiveSummationMaxMessages(event.value)
            }
            is SettingsScreenEvent.OnStickyFactsMaxMessagesChanged -> {
                updateStickyFactsMaxMessagesChanged(event.value)
            }
            is SettingsScreenEvent.OnApiKeyChanged -> draftFlow.update {
                it.copy(apiKey = event.value)
            }
            is SettingsScreenEvent.OnBaseUrlChanged -> draftFlow.update {
                it.copy(baseUrl = event.value)
            }
            is SettingsScreenEvent.OnBaseModelChanged -> draftFlow.update {
                it.copy(baseModel = event.value)
            }
            SettingsScreenEvent.OnSaveClick -> {
                save()
            }
            SettingsScreenEvent.OnResetClick -> {
                resetToSaved()
            }
        }
    }

    private fun updateContextManagementType(type: ContextManagementType) {
        draftFlow.update { draft ->
            draft.copy(contextManagementType = type)
        }
    }

    private fun updateMaxMessages(maxMessages: String) {
        val newValue = maxMessages.toIntOrNull()
        newValue?.let {
            draftFlow.update { it.copy(maxMessages = newValue) }
        }
    }

    private fun updateMaxTokens(maxTokens: String) {
        val newValue = maxTokens.toIntOrNull()
        newValue?.let {
            draftFlow.update { it.copy(maxTokens = newValue) }
        }
    }

    private fun updateRecursiveSummationMaxMessages(maxMessages: String) {
        val newValue = maxMessages.toIntOrNull()
        newValue?.let {
            draftFlow.update { it.copy(recursiveSummationMaxMessages = newValue) }
        }
    }

    private fun updateStickyFactsMaxMessagesChanged(maxMessages: String) {
        val newValue = maxMessages.toIntOrNull()
        newValue?.let {
            draftFlow.update { it.copy(stickyFactsMaxMessages = newValue) }
        }
    }

    private fun save() {
        val draft = draftFlow.value
        scope.launch {
            settingsInteractor.update(draft)
        }
    }

    private fun resetToSaved() {
        val saved = uiState.value.saved
        draftFlow.value =  saved
    }
}
