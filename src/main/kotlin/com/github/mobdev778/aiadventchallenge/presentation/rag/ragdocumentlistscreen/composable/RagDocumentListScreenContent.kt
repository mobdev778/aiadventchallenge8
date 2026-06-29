package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.RagDocumentListScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun RagDocumentListScreenContent(
    documents: List<RagDocumentListItem>,
    onEvent: (RagDocumentListScreenEvent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DefaultButton(onClick = { onEvent(RagDocumentListScreenEvent.OnBackClick) }) {
                Text("Назад")
            }

            DefaultButton(onClick = { onEvent(RagDocumentListScreenEvent.OnAddClick) }) {
                Text("Добавить")
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "RAG документы",
        )

        if (documents.isEmpty()) {
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = "Документов пока нет. Нажмите 'Добавить'.",
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = documents,
                    key = { it.id },
                ) { document ->
                    RagDocumentRow(
                        document = document,
                        onClick = {
                            onEvent(RagDocumentListScreenEvent.OnDocumentClick(document))
                        },
                        onDeleteClick = {
                            onEvent(RagDocumentListScreenEvent.OnDeleteClick(document.id))
                        },
                    )
                }
            }
        }
    }
}
