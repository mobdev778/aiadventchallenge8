package com.github.mobdev778.aiadventchallenge.domain.rag.filechunker

import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Реализация [FileChunker], которая разбивает текстовый файл на смысловые фрагменты (абзацы).
 *
 * Абзацем считается группа подряд идущих непустых строк с одинаковым количеством пробелов в начале.
 * Строки внутри абзаца разделяются символом новой строки.
 * Пустые строки и строки, состоящие только из пробелов, игнорируются.
 *
 * Использует буферизованное чтение файла в кодировке UTF-8 с заменой символов табуляции на четыре пробела
 * для единообразного подсчёта отступа.
 *
 * @property file файл, подлежащий разбиению на фрагменты.
 * @property bufferedReader читатель, открытый для файла [file] с кодировкой UTF-8.
 * @property index порядковый номер следующего фрагмента (начинается с 0). Увеличивается при каждой успешной выдаче фрагмента.
 */
class ParagraphsFileChunker(val file: File) : FileChunker {

    val bufferedReader = file.bufferedReader(StandardCharsets.UTF_8)

    private var cachedLine: String? = null
    var index = 0

    /**
     * Возвращает следующий непустой абзац файла в виде [FileChunk] или `null`, если файл закончился.
     *
     * Пропускает пустые строки до и после найденного абзаца.
     * При достижении конца файла закрывает [bufferedReader] и возвращает `null`.
     *
     * @return следующий фрагмент файла или `null`.
     */
    override fun next(): FileChunk? {
        var paragraph: String?
        do {
            paragraph = readParagraph()
        } while (paragraph?.isBlank() == true)

        if (paragraph != null) {
            return FileChunk(file, index, paragraph).also {
                index++
            }
        } else {
            bufferedReader.close()
            return null
        }
    }

    /**
     * Считывает один абзац из файла.
     *
     * Абзац формируется из первой непустой строки и всех последующих строк с тем же количеством начальных пробелов.
     * Если первая считанная строка равна `null`, возвращается `null` (конец файла).
     *
     * @return строка абзаца или `null`.
     */
    private fun readParagraph(): String? {
        val firstLine = readLine() ?: return null

        val builder = StringBuilder()
        builder.append(firstLine)

        val prefixSize = getSpacePrefixSize(firstLine)

        while (true) {
            val line = readLine() ?: break
            if (prefixSize == getSpacePrefixSize(line)) {
                if (builder.length > 0) {
                    builder.append("\n")
                }
                builder.append(line)
            } else {
                pushBackLine(line)
                break
            }
        }
        return builder.toString()
    }

    /**
     * Возвращает следующую строку из файла или строку из внутреннего кэша, если она была "откатана".
     *
     * Если во внутреннем кэше есть сохранённая строка (результат вызова [pushBackLine]), она возвращается,
     * а кэш очищается. Иначе читается новая строка из [bufferedReader] с заменой табуляций на пробелы.
     *
     * @return строка из файла или `null`, если достигнут конец файла.
     */
    private fun readLine(): String? {
        val line = if (cachedLine != null) {
            val result = cachedLine
            cachedLine = null
            result
        } else {
            bufferedReader.readLine()?.replace("\t", "    ")
        }
        return line
    }

    /**
     * Помещает строку во внутренний кэш, чтобы она была возвращена следующим вызовом [readLine].
     *
     * Используется для возврата «лишней» считанной строки, когда при чтении абзаца встретилась строка с другим отступом.
     *
     * @param line строка для сохранения в кэше.
     */
    private fun pushBackLine(line: String) {
        cachedLine = line
    }

    /**
     * Вычисляет количество начальных пробелов в строке.
     *
     * Ищет непрерывную последовательность символов пробела от начала строки и возвращает её длину.
     *
     * @param line исходная строка.
     * @return количество пробелов в начале строки.
     */
    private fun getSpacePrefixSize(line: String): Int {
        var result = 0
        for (c in line) {
           if (c == ' ') result++ else break
        }
        return result
    }
}
