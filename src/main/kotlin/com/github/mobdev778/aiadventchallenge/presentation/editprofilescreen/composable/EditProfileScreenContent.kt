package com.github.mobdev778.aiadventchallenge.presentation.editprofilescreen.composable

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
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.editprofilescreen.EditProfileScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.editprofilescreen.model.EditProfileScreenState
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.LabeledTextField
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

/**
 * Основной composable-компонент экрана редактирования профиля.
 *
 * Отображает форму для изменения имени и содержимого профиля в соответствии
 * с текущим состоянием [EditProfileScreenState]. Все пользовательские действия
 * передаются в виде типизированных событий [EditProfileScreenEvent] через
 * колбэк [onEvent], что обеспечивает соответствие архитектурному паттерну UDF
 * (Unidirectional Data Flow).
 *
 * Компонент включает:
 * - кнопку «Назад» для возврата на предыдущий экран без сохранения;
 * - декоративный заголовок [ScreenHeader] с текстом «Редактирование профиля»;
 * - поля ввода для имени (однострочное) и содержимого в формате Markdown
 *   (многострочное, с возможностью вертикальной прокрутки), построенные на базе
 *   [LabeledTextField];
 * - кнопку «Изменить» для сохранения внесённых правок.
 *
 * @param state Текущее состояние экрана редактирования профиля.
 * @param onEvent Колбэк для обработки событий, инициируемых пользователем.
 */
@Composable
fun EditProfileScreenContent(
    state: EditProfileScreenState,
    onEvent: (EditProfileScreenEvent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DefaultButton(onClick = { onEvent(EditProfileScreenEvent.OnBackClick) }) {
                Text("Назад")
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "Редактирование профиля",
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text("Название")
        LabeledTextField(
            label = "",
            value = state.name,
            onValueChange = { onEvent(EditProfileScreenEvent.OnNameChange(it)) },
        )

        Spacer(modifier = Modifier.size(12.dp))

        Text("Содержимое (.md):")
        LabeledTextField(
            label = "",
            value = state.content,
            onValueChange = { onEvent(EditProfileScreenEvent.OnContentChange(it)) },
            modifier = Modifier
                .heightIn(min = 200.dp, max = 400.dp)
                .verticalScroll(rememberScrollState()),
            singleLine = false,
        )

        Spacer(modifier = Modifier.size(16.dp))

        DefaultButton(onClick = { onEvent(EditProfileScreenEvent.OnEditClick) }) {
            Text("Изменить")
        }
    }
}
