package com.github.mobdev778.aiadventchallenge.domain.contextmanagement.stickyFacts

import org.koin.core.annotation.Single

@Single
class ConvertTextToMapUseCase {

    val divider = ":"

    fun invoke(inputData: String): Map<String, String> {
        val lines = inputData.split("\n")

        return lines.mapNotNull { line ->
            val index = line.indexOf(divider)
            if (index == -1) return@mapNotNull null

            val parts = line.split(divider)
            val key = parts[0].trim().replace("\"", "").replace("\'", "")
            val value = parts[1].trim().replace("\"", "").replace("\'", "")
            key to value
        }.toMap()
    }
}
