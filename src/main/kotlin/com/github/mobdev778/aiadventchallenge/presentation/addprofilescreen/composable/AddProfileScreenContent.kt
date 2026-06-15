package com.github.mobdev778.aiadventchallenge.presentation.addprofilescreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.addprofilescreen.AddProfileScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.addprofilescreen.model.AddProfileScreenState
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.LabeledTextField
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun AddProfileScreenContent(
    state: AddProfileScreenState,
    onEvent: (AddProfileScreenEvent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DefaultButton(onClick = { onEvent(AddProfileScreenEvent.OnBackClick) }) {
                Text("Назад")
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "Добавление профиля",
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text("Название")
        LabeledTextField(
            label = "",
            value = state.name,
            onValueChange = { onEvent(AddProfileScreenEvent.OnNameChange(it)) },
        )

        Spacer(modifier = Modifier.size(12.dp))

        Text("Содержимое (.md):")
        LabeledTextField(
            label = "",
            value = state.content,
            onValueChange = { onEvent(AddProfileScreenEvent.OnContentChange(it)) },
            modifier = Modifier
                // ~10 visible lines minimum, but no more than ~20 lines height.
                // (line height is approximated via dp; exact value depends on font metrics)
                .heightIn(min = 200.dp, max = 400.dp)
                // show scrollbar/scroll via mouse wheel/trackpad
                .verticalScroll(rememberScrollState()),
            singleLine = false,
        )

        Spacer(modifier = Modifier.size(16.dp))

        DefaultButton(onClick = { onEvent(AddProfileScreenEvent.OnAddClick) }) {
            Text("Добавить")
        }
    }
}
