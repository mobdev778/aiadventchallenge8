package com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagConfigRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagConfig
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
class RagConfigScreenStateHolder(
    private val ragConfigRepository: RagConfigRepository,
    private val scope: CoroutineScope,
) {

    val uiState: StateFlow<RagConfig> = ragConfigRepository
        .observeConfig()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RagConfigRepository.default,
        )

    val commands = MutableSharedFlow<RagConfigScreenCommand>(
        extraBufferCapacity = 1,
    )

    fun onEvent(event: RagConfigScreenEvent) {
        when (event) {
            RagConfigScreenEvent.OnBackClick -> {
                commands.tryEmit(RagConfigScreenCommand.Back)
            }
            is RagConfigScreenEvent.OnTopKBeforeChanged -> updateConfig {
                copy(topKBefore = event.value)
            }
            is RagConfigScreenEvent.OnFilterTypeChanged -> updateConfig {
                copy(filterType = event.value)
            }
            is RagConfigScreenEvent.OnUseQueryRewritingChanged -> updateConfig {
                copy(useQueryRewriting = event.value)
            }
            is RagConfigScreenEvent.OnTopKAfterChanged -> updateConfig {
                copy(topKAfter = event.value)
            }
            is RagConfigScreenEvent.OnUseMinSimilarityChanged -> updateConfig {
                copy(useMinSimilarity = event.value)
            }
            is RagConfigScreenEvent.OnMinSimilarityChanged -> updateConfig {
                copy(minSimilarity = event.value)
            }
            is RagConfigScreenEvent.OnChooseRankerModelPathClick -> chooseFile(FileType.RankModel)
            is RagConfigScreenEvent.OnRankerModelPathChanged -> updateConfig {
                copy(rankerModelPath = event.value)
            }
            is RagConfigScreenEvent.OnChooseRankerTokenizerPathClick -> chooseFile(FileType.RankTokenizer)
            is RagConfigScreenEvent.OnRankerTokenizerPathChanged -> updateConfig {
                copy(rankerTokenizerPath = event.value)
            }
            is RagConfigScreenEvent.OnChooseEmbModelPathClick -> chooseFile(FileType.EmbModel)
            is RagConfigScreenEvent.OnEmbModelPathChanged -> updateConfig {
                copy(embModelPath = event.value)
            }
            is RagConfigScreenEvent.OnChooseEmbTokenizerPathClick -> chooseFile(FileType.EmbTokenizer)
            is RagConfigScreenEvent.OnEmbTokenizerPathChanged -> updateConfig {
                copy(embTokenizerPath = event.value)
            }
        }
    }

    private fun chooseFile(action: FileType) {
        val descriptor = when (action) {
            FileType.RankModel -> FileChooserDescriptorFactory.createSingleFileDescriptor("onnx")
            FileType.RankTokenizer -> FileChooserDescriptorFactory.createSingleFileDescriptor("json")
            FileType.EmbModel -> FileChooserDescriptorFactory.createSingleFileDescriptor("onnx")
            FileType.EmbTokenizer -> FileChooserDescriptorFactory.createSingleFileDescriptor("json")
        }
        val chosenFile: VirtualFile? = FileChooser.chooseFile(descriptor, null, null)
        val path = chosenFile?.path ?: return
        scope.launch(Dispatchers.IO) {
            val oldConfig = ragConfigRepository.getConfig()
            val updatedConfig = when (action) {
                FileType.RankModel -> oldConfig.copy(rankerModelPath = path)
                FileType.RankTokenizer -> oldConfig.copy(rankerTokenizerPath = path)
                FileType.EmbModel -> oldConfig.copy(embModelPath = path)
                FileType.EmbTokenizer -> oldConfig.copy(embTokenizerPath = path)
            }
            ragConfigRepository.updateConfig(updatedConfig)
        }
    }

    private fun updateConfig(transform: RagConfig.() -> RagConfig) {
        scope.launch(Dispatchers.IO) {
            val updatedConfig = ragConfigRepository.getConfig().transform()
            ragConfigRepository.updateConfig(updatedConfig)
        }
    }

    enum class FileType {
        RankModel,
        RankTokenizer,
        EmbModel,
        EmbTokenizer,
    }
}
