package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.mapper

import com.github.mobdev778.aiadventchallenge.domain.settings.model.ContextManagementType
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.model.ContextManagementTypeUi

object ContextManagementTypeMapper {

    fun map(contextManagementType: ContextManagementType, selected: Boolean): ContextManagementTypeUi {
        return ContextManagementTypeUi(
            type = contextManagementType,
            label = when (contextManagementType) {
                ContextManagementType.None -> "- Не используется -"
                ContextManagementType.SlidingWindow -> "Sliding Window"
                ContextManagementType.StickyFacts -> "Sticky Facts"
                ContextManagementType.Branching -> "Branching"
            },
            selected = selected,
        )
    }
}
