package com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.AddRagDocumentScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model.AddRagDocumentScreenState
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.LabeledDropdown
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.LabeledTextField
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

/**
 * Основное содержимое экрана добавления RAG-документа.
 *
 * Формирует пользовательский интерфейс, который позволяет задать параметры
 * нового документа для Retrieval-Augmented Generation: источник (путь к файлу),
 * заголовок и стратегию разбиения текста на чанки.
 *
 * Экран реализует паттерн UDF (Unidirectional Data Flow): принимает текущее
 * состояние [state] и функцию [onEvent] для отправки событий
 * [AddRagDocumentScreenEvent] в вышестоящий слой (ViewModel).
 *
 * Основные элементы интерфейса:
 * - кнопка "Назад" — вызывает событие [AddRagDocumentScreenEvent.OnBackClick];
 * - заголовок [ScreenHeader];
 * - текстовое поле для пути к файлу и кнопка "Выбрать...", отправляющая
 *   [AddRagDocumentScreenEvent.OnChooseSourceClick];
 * - поле для ввода названия документа ([AddRagDocumentScreenEvent.OnTitleChange]);
 * - выпадающий список [LabeledDropdown] для выбора стратегии чанкинга
 *   ([AddRagDocumentScreenEvent.OnChunkingStrategyChange]);
 * - кнопка "Добавить", инициирующая сохранение документа
 *   ([AddRagDocumentScreenEvent.OnAddClick]).
 *
 * @param state Текущее состояние экрана, содержащее значения полей ввода.
 * @param onEvent Callback-функция для обработки событий, производимых пользователем.
 */
@Composable
fun AddRagDocumentScreenContent(
    state: AddRagDocumentScreenState,
    onEvent: (AddRagDocumentScreenEvent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DefaultButton(onClick = { onEvent(AddRagDocumentScreenEvent.OnBackClick) }) {
                Text("Назад")
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "Добавление RAG документа",
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text("Путь к файлу или папке")
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LabeledTextField(
                label = "",
                value = state.source,
                onValueChange = { onEvent(AddRagDocumentScreenEvent.OnSourceChange(it)) },
                modifier = Modifier.weight(1f),
            )

            DefaultButton(
                modifier = Modifier.padding(start = 8.dp),
                onClick = { onEvent(AddRagDocumentScreenEvent.OnChooseSourceClick) },
            ) {
                Text("Выбрать...")
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        Text("Название")
        LabeledTextField(
            label = "",
            value = state.title,
            onValueChange = { onEvent(AddRagDocumentScreenEvent.OnTitleChange(it)) },
        )

        Spacer(modifier = Modifier.size(12.dp))

        LabeledDropdown(
            label = "Стратегия Chunking",
            labelColor = Color.Unspecified,
            selectedText = state.chunkingStrategy.title,
            items = AddRagDocumentScreenState.ChunkingStrategy.entries.map { it.title },
            onItemSelected = { onEvent(AddRagDocumentScreenEvent.OnChunkingStrategyChange(it)) },
        )

        Spacer(modifier = Modifier.size(16.dp))

        DefaultButton(onClick = { onEvent(AddRagDocumentScreenEvent.OnAddClick) }) {
            Text("Добавить")
        }
    }
}
