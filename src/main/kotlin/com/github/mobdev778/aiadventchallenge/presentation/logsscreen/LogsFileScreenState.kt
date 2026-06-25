package com.github.mobdev778.aiadventchallenge.presentation.logsscreen

data class LogsFileScreenState(
    val fileName: String = "",
    val logs: List<String> = emptyList(),
)
