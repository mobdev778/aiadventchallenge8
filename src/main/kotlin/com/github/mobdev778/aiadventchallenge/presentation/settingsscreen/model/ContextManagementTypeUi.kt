package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.model

import com.github.mobdev778.aiadventchallenge.domain.settings.model.ContextManagementType

data class ContextManagementTypeUi(
    val type: ContextManagementType,
    val label: String,
    val selected: Boolean,
)

fun List<ContextManagementTypeUi>.getSelected(): ContextManagementType {
    return first { it.selected }.type
}