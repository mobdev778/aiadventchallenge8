package com.github.mobdev778.aiadventchallenge.presentation.editprofilescreen

sealed interface EditProfileScreenCommand {
    data object Back : EditProfileScreenCommand
}
