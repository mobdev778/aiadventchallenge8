package com.github.mobdev778.aiadventchallenge.domain.contextmanagement.stickyFacts

import org.koin.core.annotation.Single

/**
 * UseCase для преобразования словаря строк (Map<String, String>) в текстовое представление.
 *
 * Реализован как синглтон в Koin-контексте. Каждая пара "ключ-значение" отображается в отдельной строке,
 * разделённой символом [:]. Формат используется для сохранения или обработки контекстных фактов
 * (sticky facts) в доменном слое.
 */
@Single
class ConvertMapToTextUseCase {

    /** Разделитель между ключом и значением в результирующей строке. */
    val divider = ":"

    /**
     * Преобразует переданный ассоциативный массив [inputData] в многострочный текст.
     *
     * @param inputData отображение String -> String, содержащее закреплённые факты.
     * @return строка, в которой каждая запись имеет формат `<ключ>:<значение>` и завершается символом новой строки.
     */
    fun invoke(inputData: Map<String, String>): String {
        val builder = StringBuilder()
        for ((key, value) in inputData) {
            builder.append(key).append(divider).append(value).append("\n")
        }
        return builder.toString()
    }
}
