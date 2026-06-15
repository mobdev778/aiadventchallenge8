package com.github.mobdev778.aiadventchallenge.presentation.addprofilescreen

sealed interface AddProfileScreenEvent {
    data object OnBackClick : AddProfileScreenEvent
    data class OnNameChange(val value: String) : AddProfileScreenEvent
    data class OnContentChange(val value: String) : AddProfileScreenEvent
    data object OnAddClick : AddProfileScreenEvent
}
