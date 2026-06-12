package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.model

import com.github.mobdev778.aiadventchallenge.domain.settings.model.AppSettings

data class SettingsScreenState(
    val saved: AppSettings,
    val draft: AppSettings,
    val messageSelectionTypes: List<MessageSelectionTypeUi>,
    val actionEnabled: Boolean,
)