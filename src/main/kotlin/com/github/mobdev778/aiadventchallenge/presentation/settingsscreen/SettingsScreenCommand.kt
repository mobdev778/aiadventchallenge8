package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen

sealed interface SettingsScreenCommand {
    data object Back : SettingsScreenCommand
    data object OpenProfiles : SettingsScreenCommand
    data object OpenRag : SettingsScreenCommand
    data object OpenMcp : SettingsScreenCommand
    data object OpenMyMcp : SettingsScreenCommand
}