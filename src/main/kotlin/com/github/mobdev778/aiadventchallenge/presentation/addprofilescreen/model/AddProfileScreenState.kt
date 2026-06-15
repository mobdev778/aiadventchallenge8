package com.github.mobdev778.aiadventchallenge.presentation.addprofilescreen.model

import androidx.compose.runtime.Immutable

@Immutable
data class AddProfileScreenState(
    val name: String = "",
    val content: String = "",
)
