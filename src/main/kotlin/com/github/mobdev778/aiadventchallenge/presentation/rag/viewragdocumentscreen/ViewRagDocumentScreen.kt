package com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem
import com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen.composable.ViewRagDocumentScreenContent
import org.koin.java.KoinJavaComponent.inject

/**
 * Экран просмотра содержимого документа RAG-системы.
 *
 * Отображает детальную информацию о выбранном документе, его фрагменты или другие данные,
 * связанные с Retrieval-Augmented Generation. Экран управляет жизненным циклом
 * [ViewRagDocumentScreenStateHolder], обрабатывает навигационные команды и передаёт
 * события в состояние через [ViewRagDocumentScreenStateHolder.onEvent].
 *
 * @param document Модель элемента документа, выбранного из списка. Содержит идентификатор,
 *                 источник, заголовок и количество чанков.
 * @param onBack   Колбэк, вызываемый при необходимости вернуться на предыдущий экран,
 *                 например, при получении команды [ViewRagDocumentScreenCommand.Back].
 */
@Composable
fun ViewRagDocumentScreen(
    document: RagDocumentListItem,
    onBack: () -> Unit,
) {
    val stateHolder = remember {
        inject<ViewRagDocumentScreenStateHolder>(ViewRagDocumentScreenStateHolder::class.java).value
    }

    val state by stateHolder.state.collectAsState()

    LaunchedEffect(document) {
        stateHolder.start(document)
    }

    ViewRagDocumentScreenContent(
        state = state,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(Unit) {
        stateHolder.commands.collect { command ->
            when (command) {
                ViewRagDocumentScreenCommand.Back -> onBack()
            }
        }
    }
}
