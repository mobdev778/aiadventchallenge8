package com.github.mobdev778.aiadventchallenge.domain.contextmanagement.stickyFacts

import org.koin.core.annotation.Single

/**
 * Use case преобразования текстового представления "липких фактов" в карту ключ-значение.
 *
 * Используется в слое управления контекстом для обработки фактов, заданных в виде строк,
 * где каждая строка содержит пару "ключ:значение". Ключи и значения предварительно очищаются от кавычек и пробелов.
 * Строки без разделителя ":" игнорируются.
 */
@Single
class ConvertTextToMapUseCase {

    /** Символ-разделитель между ключом и значением. */
    val divider = ":"

    /**
     * Преобразует входную строку, состоящую из строк формата "ключ:значение", в [Map].
     *
     * @param inputData строка с фактами, разделёнными переводом строки.
     * @return карта, где ключи и значения извлечены из строк и очищены от кавычек и лишних пробелов.
     */
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
