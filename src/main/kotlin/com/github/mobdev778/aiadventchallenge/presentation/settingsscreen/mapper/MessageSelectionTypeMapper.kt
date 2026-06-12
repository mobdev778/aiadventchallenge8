package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.mapper

import com.github.mobdev778.aiadventchallenge.domain.settings.model.MessageSelectionType
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.model.MessageSelectionTypeUi

object MessageSelectionTypeMapper {

    fun map(messageSelectionType: MessageSelectionType, selected: Boolean): MessageSelectionTypeUi {
        return MessageSelectionTypeUi(
            type = messageSelectionType,
            label = when (messageSelectionType) {
                MessageSelectionType.FullHistory -> "- Не используется -"
                MessageSelectionType.MessageLimit -> "Лимит сообщений"
                MessageSelectionType.TokenLimit -> "Лимит токенов"
                MessageSelectionType.RecursiveSummation -> "\uD83D\uDE0E Рекурсивная суммаризация \uD83D\uDE0E"
            },
            selected = selected,
        )
    }
}