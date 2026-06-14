package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen

sealed interface SettingsScreenCommand {
    data object Back : SettingsScreenCommand
}