package com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen

import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model.AddRagDocumentScreenState
import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model.AddRagDocumentScreenState.ChunkingStrategy
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single

@Single
class AddRagDocumentScreenStateHolder(
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(AddRagDocumentScreenState())
    val state: StateFlow<AddRagDocumentScreenState> = _state.asStateFlow()

    val commands = MutableSharedFlow<AddRagDocumentScreenCommand>(
        extraBufferCapacity = 1,
    )

    fun onEvent(event: AddRagDocumentScreenEvent) {
        when (event) {
            AddRagDocumentScreenEvent.OnBackClick -> commands.tryEmit(AddRagDocumentScreenCommand.Back)
            AddRagDocumentScreenEvent.OnChooseSourceClick -> chooseSource()
            is AddRagDocumentScreenEvent.OnSourceChange -> _state.update { it.copy(source = event.value) }
            is AddRagDocumentScreenEvent.OnTitleChange -> _state.update { it.copy(title = event.value) }
            is AddRagDocumentScreenEvent.OnChunkingStrategyChange -> updateChunkingStrategy(event.index)
            AddRagDocumentScreenEvent.OnAddClick -> openAddingScreen()
        }
    }

    private fun chooseSource() {
        val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
        val chosenFile: VirtualFile? = FileChooser.chooseFile(descriptor, null, null)
        val path = chosenFile?.path ?: return
        _state.update { current -> current.copy(source = path) }
    }

    private fun updateChunkingStrategy(index: Int) {
        val strategy = ChunkingStrategy.entries.getOrNull(index) ?: return
        _state.update { current -> current.copy(chunkingStrategy = strategy) }
    }

    private fun openAddingScreen() {
        val snapshot = state.value
        val source = snapshot.source.trim()
        val title = snapshot.title.trim()
        if (source.isEmpty() || title.isEmpty()) return
        commands.tryEmit(
            AddRagDocumentScreenCommand.OpenAddingDocument(
                source = source,
                title = title,
                chunkingStrategy = snapshot.chunkingStrategy,
            )
        )
    }
}
