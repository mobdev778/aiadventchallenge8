package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.model.SettingsScreenState
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider

@Suppress("MagicNumber")
private val NeonHighlightedTextColor = Color(0xFF04D9FF)

@Composable
fun SettingsScreenContent(
    state: SettingsScreenState,
    onEvent: (SettingsScreenEvent) -> Unit,
) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SettingsHeaderSection(onEvent = onEvent)

        NavigationButtonsRow(onEvent = onEvent)

        Spacer(modifier = Modifier.size(16.dp))

        ContextManagementTypeTypeBlock(
            titleColor = NeonHighlightedTextColor,
            items = state.contextManagementTypes,
            maxMessages = state.draft.maxMessages,
            stickyFactsMaxMessages = state.draft.stickyFactsMaxMessages,
            onEvent = onEvent,
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            orientation = Orientation.Horizontal,
        )

        LlmSettingsBlock(
            apiKey = state.draft.apiKey,
            baseUrl = state.draft.baseUrl,
            baseModel = state.draft.baseModel,
            titleColor = NeonHighlightedTextColor,
            onEvent = onEvent,
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            orientation = Orientation.Horizontal,
        )

        SettingsActionButtons(state = state, onEvent = onEvent)
    }
}
