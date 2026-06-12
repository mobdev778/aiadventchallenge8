package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.model

import com.github.mobdev778.aiadventchallenge.domain.settings.model.MessageSelectionType

data class MessageSelectionTypeUi(
    val type: MessageSelectionType,
    val label: String,
    val selected: Boolean,
)

fun List<MessageSelectionTypeUi>.getSelected(): MessageSelectionType {
    return first { it.selected }.type
}