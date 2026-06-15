package com.github.mobdev778.aiadventchallenge.presentation.addprofilescreen

sealed interface AddProfileScreenCommand {
    data object Back : AddProfileScreenCommand
}
