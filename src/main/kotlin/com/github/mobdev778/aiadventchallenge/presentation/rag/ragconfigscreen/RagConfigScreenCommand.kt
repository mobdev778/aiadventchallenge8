package com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen

sealed interface RagConfigScreenCommand {
    data object Back : RagConfigScreenCommand
}
