package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.composable.RagDocumentListScreenContent
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem
import org.koin.java.KoinJavaComponent.inject

/**
 * Composable-функция экрана со списком RAG-документов.
 *
 * Является точкой входа для экрана списка документов Retrieval-Augmented Generation (RAG)
 * и координирует взаимодействие между UI-состоянием, пользовательскими событиями и навигацией.
 *
 * Компонент создаёт экземпляр [RagDocumentListScreenStateHolder] с помощью Koin,
 * подписывается на поток его [RagDocumentListScreenState] и передаёт актуальное состояние
 * в дочерний composable-контент [RagDocumentListScreenContent].
 * Также запускает сбор команд от держателя состояния в `LaunchedEffect` и преобразует их
 * в вызовы соответствующих навигационных коллбэков, переданных из родительского компонента.
 *
 * @param onBack Коллбэк, вызываемый при необходимости вернуться на предыдущий экран.
 * @param onOpenAddDocument Коллбэк, инициирующий переход на экран добавления нового RAG-документа.
 * @param onOpenDocument Коллбэк, открывающий детальный просмотр выбранного документа.
 *                       Принимает модель [RagDocumentListItem], содержащую идентификатор, источник,
 *                       заголовок и количество чанков документа.
 * @param onOpenRagConfig Коллбэк для перехода на экран конфигурации RAG-системы.
 */
@Composable
fun RagDocumentListScreen(
    onBack: () -> Unit,
    onOpenAddDocument: () -> Unit,
    onOpenDocument: (RagDocumentListItem) -> Unit,
    onOpenRagConfig: () -> Unit,
) {
    val stateHolder = remember {
        inject<RagDocumentListScreenStateHolder>(RagDocumentListScreenStateHolder::class.java).value
    }

    val uiState by stateHolder.uiState.collectAsState()

    RagDocumentListScreenContent(
        documents = uiState.documents,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(Unit) {
        stateHolder.commands.collect { command ->
            when (command) {
                RagDocumentListScreenCommand.Back -> onBack()
                RagDocumentListScreenCommand.OpenAddDocument -> onOpenAddDocument()
                RagDocumentListScreenCommand.OpenRagConfig -> onOpenRagConfig()
                is RagDocumentListScreenCommand.OpenDocument -> onOpenDocument(command.document)
            }
        }
    }
}
