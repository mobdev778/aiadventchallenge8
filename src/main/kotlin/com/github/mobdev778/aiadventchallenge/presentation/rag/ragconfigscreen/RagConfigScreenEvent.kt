package com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen

import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagFilterType

sealed interface RagConfigScreenEvent {
    data object OnBackClick : RagConfigScreenEvent
    data class OnTopKBeforeChanged(val value: Int) : RagConfigScreenEvent
    data class OnFilterTypeChanged(val value: RagFilterType) : RagConfigScreenEvent
    data class OnUseQueryRewritingChanged(val value: Boolean) : RagConfigScreenEvent
    data class OnTopKAfterChanged(val value: Int) : RagConfigScreenEvent
    data class OnUseMinSimilarityChanged(val value: Boolean) : RagConfigScreenEvent
    data class OnMinSimilarityChanged(val value: Double) : RagConfigScreenEvent
    data object OnChooseRankerModelPathClick : RagConfigScreenEvent
    data class OnRankerModelPathChanged(val value: String) : RagConfigScreenEvent
    data object OnChooseRankerTokenizerPathClick : RagConfigScreenEvent
    data class OnRankerTokenizerPathChanged(val value: String) : RagConfigScreenEvent
    data object OnChooseEmbModelPathClick : RagConfigScreenEvent
    data class OnEmbModelPathChanged(val value: String) : RagConfigScreenEvent
    data object OnChooseEmbTokenizerPathClick : RagConfigScreenEvent
    data class OnEmbTokenizerPathChanged(val value: String) : RagConfigScreenEvent
}
