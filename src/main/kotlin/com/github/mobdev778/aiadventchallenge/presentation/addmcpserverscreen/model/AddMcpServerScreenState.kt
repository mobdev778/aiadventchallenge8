package com.github.mobdev778.aiadventchallenge.presentation.addmcpserverscreen.model

import androidx.compose.runtime.Immutable

@Immutable
data class AddMcpServerScreenState(
    val name: String = "",
    val url: String = "",
)
