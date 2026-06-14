package com.github.mobdev778.aiadventchallenge.domain.contextmanagement.stickyFacts

import org.koin.core.annotation.Single

@Single
class ConvertMapToTextUseCase {

    val divider = ":"

    fun invoke(inputData: Map<String, String>): String {
        val builder = StringBuilder()
        for ((key, value) in inputData) {
            builder.append(key).append(divider).append(value).append("\n")
        }
        return builder.toString()
    }
}