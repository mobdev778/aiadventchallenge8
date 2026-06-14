package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen

import com.github.mobdev778.aiadventchallenge.domain.settings.model.ContextManagementType

sealed interface SettingsScreenEvent {
    data object OnBackClick : SettingsScreenEvent

    data class OnContextManagementTypeChanged(val type: ContextManagementType) : SettingsScreenEvent

    data class OnMaxMessagesChanged(val value: String) : SettingsScreenEvent
    data class OnMaxTokensChanged(val value: String) : SettingsScreenEvent
    data class OnStickyFactsMaxMessagesChanged(val value: String) : SettingsScreenEvent
    data class OnRecursiveSummationMaxMessagesChanged(val value: String) : SettingsScreenEvent
    data class OnApiKeyChanged(val value: String) : SettingsScreenEvent
    data class OnBaseUrlChanged(val value: String) : SettingsScreenEvent
    data class OnBaseModelChanged(val value: String) : SettingsScreenEvent

    data object OnSaveClick : SettingsScreenEvent
    data object OnResetClick : SettingsScreenEvent
}
