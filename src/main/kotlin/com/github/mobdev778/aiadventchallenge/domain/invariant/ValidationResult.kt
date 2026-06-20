package com.github.mobdev778.aiadventchallenge.domain.invariant

sealed class ValidationResult {
    object Passed : ValidationResult()
    data class Failed(val reason: String) : ValidationResult()
}