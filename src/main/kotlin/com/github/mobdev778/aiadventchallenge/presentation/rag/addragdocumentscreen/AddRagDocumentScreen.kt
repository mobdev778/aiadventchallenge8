package com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.composable.AddRagDocumentScreenContent
import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model.AddRagDocumentScreenState
import org.koin.java.KoinJavaComponent.inject

/**
 * Экран добавления нового документа в RAG (Retrieval-Augmented Generation).
 *
 * Управляет отображением формы ввода данных документа, делегирует управление состоянием
 * [AddRagDocumentScreenStateHolder] и обрабатывает одноразовые навигационные команды.
 *
 * @param onBack Колбэк для перехода назад (закрытие экрана).
 * @param onOpenAddingDocument Колбэк для открытия диалога или экрана подтверждения добавления
 * с переданными параметрами: источник, заголовок и стратегия разбиения на чанки.
 */
@Composable
fun AddRagDocumentScreen(
    onBack: () -> Unit,
    onOpenAddingDocument: (String, String, AddRagDocumentScreenState.ChunkingStrategy) -> Unit,
) {
    val stateHolder = remember {
        inject<AddRagDocumentScreenStateHolder>(AddRagDocumentScreenStateHolder::class.java).value
    }

    val state by stateHolder.state.collectAsState()

    AddRagDocumentScreenContent(
        state = state,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(Unit) {
        stateHolder.commands.collect { command ->
            when (command) {
                AddRagDocumentScreenCommand.Back -> onBack()
                is AddRagDocumentScreenCommand.OpenAddingDocument -> {
                    onOpenAddingDocument(
                        command.source,
                        command.title,
                        command.chunkingStrategy,
                    )
                }
            }
        }
    }
}
