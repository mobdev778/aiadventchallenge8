package com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen.composable.AddingRagDocumentScreenContent
import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model.AddRagDocumentScreenState
import org.koin.java.KoinJavaComponent.inject

/**
 * Главная экранная композиция этапа добавления RAG-документа.
 *
 * Управляет жизненным циклом операции загрузки и индексации документа,
 * связывая состояние экрана [AddingRagDocumentScreenStateHolder] с UI-компонентом
 * [AddingRagDocumentScreenContent] и навигационными колбэками.
 *
 * Экран инициирует процесс добавления документа с заданными параметрами
 * ([source], [title], [chunkingStrategy]) и отслеживает команды от [stateHolder],
 * чтобы выполнить возврат к форме добавления или к списку документов.
 *
 * @param source Источник документа (URL, путь к файлу или иной идентификатор).
 * @param title Заголовок документа, введённый пользователем.
 * @param chunkingStrategy Стратегия разбиения текста на чанки, выбранная пользователем.
 * @param onBackToAddDocument Колбэк для навигации обратно к экрану добавления документа.
 * @param onBackToDocumentList Колбэк для навигации к списку документов.
 */
@Composable
fun AddingRagDocumentScreen(
    source: String,
    title: String,
    chunkingStrategy: AddRagDocumentScreenState.ChunkingStrategy,
    onBackToAddDocument: () -> Unit,
    onBackToDocumentList: () -> Unit,
) {
    val stateHolder = remember {
        inject<AddingRagDocumentScreenStateHolder>(AddingRagDocumentScreenStateHolder::class.java).value
    }

    val state by stateHolder.state.collectAsState()

    LaunchedEffect(source, title, chunkingStrategy) {
        stateHolder.start(
            source = source,
            title = title,
            chunkingStrategy = chunkingStrategy,
        )
    }

    AddingRagDocumentScreenContent(
        state = state,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(Unit) {
        stateHolder.commands.collect { command ->
            when (command) {
                AddingRagDocumentScreenCommand.BackToAddDocument -> onBackToAddDocument()
                AddingRagDocumentScreenCommand.BackToDocumentList -> onBackToDocumentList()
            }
        }
    }
}
