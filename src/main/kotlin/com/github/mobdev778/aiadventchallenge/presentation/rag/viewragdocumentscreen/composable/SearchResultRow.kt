package com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen.model.ViewRagDocumentSearchResult
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Text

/**
 * Composable-функция для отображения одной строки результата поиска в RAG-документе.
 *
 * Выводит разделитель, источник, раздел и текст результата. Используется
 * в списке результатов на экране просмотра RAG-документа.
 *
 * @param result модель данных результата поиска, содержащая источник, раздел и текст.
 */
@Composable
fun SearchResultRow(
    result: ViewRagDocumentSearchResult,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            orientation = Orientation.Horizontal,
        )
        Text(text = result.source)
        Text(text = "section: ${result.section}")
        Text(
            text = result.text,
            maxLines = 30,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
