package com.github.mobdev778.aiadventchallenge.domain.invariant

import org.koin.core.annotation.Single

/**
 * Реестр инвариантов, предназначенный для централизованного хранения и применения
 * бизнес-правил (инвариантов) к парам "запрос-ответ".
 *
 * Использует аннотацию [@Single](org.koin.core.annotation.Single) из Koin,
 * чтобы гарантировать единственный экземпляр реестра в рамках приложения.
 *
 * При инициализации автоматически регистрируются базовые инварианты, такие как
 * [BannedWords] с предопределённым набором запрещённых терминов.
 */
@Single
class InvariantRegistry {

    /**
     * Внутренний изменяемый список зарегистрированных инвариантов.
     */
    private val invariants = mutableListOf<Invariant>()

    init {
        invariants.add(
            BannedWords(
                bannedTerms = setOf("наркоша", "проститутка", "VPN"),
            )
        )
    }

    /**
     * Возвращает полный список зарегистрированных инвариантов.
     *
     * @return актуальный список инвариантов [Invariant].
     */
    fun getInvariants(): List<Invariant> {
        return invariants
    }

    /**
     * Последовательно применяет все зарегистрированные инварианты к переданной паре
     * "запрос-ответ". При первом же нарушении (результат [ValidationResult.Failed])
     * проверка прерывается, и возвращается причина отказа.
     *
     * @param request текст исходного запроса пользователя
     * @param response текст ответа ассистента
     * @return [ValidationResult.Passed], если все инварианты соблюдены, иначе
     *         результат неудачной проверки первого нарушенного инварианта.
     */
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
