package com.github.mobdev778.aiadventchallenge.presentation.editprofilescreen

sealed interface EditProfileScreenEvent {
    data object OnBackClick : EditProfileScreenEvent
    data class OnNameChange(val value: String) : EditProfileScreenEvent
    data class OnContentChange(val value: String) : EditProfileScreenEvent
    data object OnEditClick : EditProfileScreenEvent
}
