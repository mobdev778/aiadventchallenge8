package com.github.mobdev778.aiadventchallenge.domain.invariant

import org.koin.core.annotation.Single

@Single
class InvariantRegistry {

    private val invariants = mutableListOf<Invariant>()

    init {
        invariants.add(
            BannedWords(
                bannedTerms = setOf("наркоша", "проститутка", "VPN"),
            )
        )
    }

    fun getInvariants(): List<Invariant> {
        return invariants
    }

    fun validate(request: String, response: String): ValidationResult {
        invariants.forEach {
            val result = it.validate(request, response)
            if (result is ValidationResult.Failed) {
                return result
            }
        }
        return ValidationResult.Passed
    }
}