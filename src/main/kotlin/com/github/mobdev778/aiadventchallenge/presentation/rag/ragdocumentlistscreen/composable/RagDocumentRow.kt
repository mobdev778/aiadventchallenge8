package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

/**
 * Composable-компонент, отображающий одну строку в списке RAG-документов.
 *
 * Строка содержит основную информацию о документе (заголовок, источник, количество чанков)
 * и кнопку "Удалить". Вся строка кликабельна, что позволяет выполнить действие при выборе элемента.
 * Используется на экране просмотра и управления RAG-документами.
 *
 * @param document Модель элемента списка RAG-документов, содержащая идентификатор,
 *                 источник, заголовок и количество чанков.
 * @param onClick Обработчик клика по строке документа (выполняется при касании в любой области строки).
 * @param onDeleteClick Обработчик нажатия на кнопку "Удалить".
 */
@Composable
fun RagDocumentRow(
    document: RagDocumentListItem,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = document.title,
                fontWeight = FontWeight.Bold,
            )
            Text(text = document.source)
            Text(text = "chunks: ${document.chunkCount}")
        }

        Spacer(modifier = Modifier.width(8.dp))

        DefaultButton(onClick = onDeleteClick) {
            Text("Удалить")
        }
    }
}
