package com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen.ViewRagDocumentScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen.model.ViewRagDocumentScreenState
import com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen.model.ViewRagDocumentSearchResult
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.LabeledTextField
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

/**
 * Контентная область экрана просмотра RAG-документа. Отображает навигационную
 * панель, заголовок, информацию о выбранном документе, поле поискового запроса,
 * кнопку поиска и список результатов семантического поиска по фрагментам.
 *
 * Элементы экрана реагируют на события пользователя, передаваемые через единый
 * колбэк [onEvent]: возврат к предыдущему экрану, изменение поискового запроса
 * и запуск поиска.
 *
 * @param state Текущее состояние экрана, включая документ, поисковый запрос,
 *   индикатор загрузки и результаты поиска.
 * @param onEvent Лямбда-обработчик событий экрана. Принимает экземпляры
 *   [ViewRagDocumentScreenEvent] для выполнения соответствующих действий.
 */
@Composable
fun ViewRagDocumentScreenContent(
    state: ViewRagDocumentScreenState,
    onEvent: (ViewRagDocumentScreenEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DefaultButton(onClick = { onEvent(ViewRagDocumentScreenEvent.OnBackClick) }) {
                Text("Назад")
            }
        }

        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "RAG документ",
        )

        state.document?.let { document ->
            Text(text = document.title)
            Text(text = document.source)
            Text(text = "Фрагменты: ${document.chunkCount}")
        }

        LabeledTextField(
            label = "Текст для поиска",
            value = state.query,
            onValueChange = { onEvent(ViewRagDocumentScreenEvent.OnQueryChange(it)) },
            singleLine = false,
            modifier = Modifier.fillMaxWidth(),
        )

        DefaultButton(
            modifier = Modifier
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyUp && event.key == Key.Enter) {
                        onEvent(ViewRagDocumentScreenEvent.OnSearchClick)
                        true
                    } else {
                        false
                    }
                },
            onClick = { onEvent(ViewRagDocumentScreenEvent.OnSearchClick) }
        ) {
            Text("Найти")
        }

        if (state.isSearching || state.results.isNotEmpty()) {
            Text(text = "Поиск")
        }

        if (state.results.isNotEmpty()) {
            SearchResultsList(results = state.results)
        }
    }
}

@Composable
private fun SearchResultsList(results: List<ViewRagDocumentSearchResult>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(results) { result ->
            SearchResultRow(result = result)
        }
    }
}
