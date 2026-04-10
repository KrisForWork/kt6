package com.example.domain.repository

import com.example.domain.model.Category
import com.example.domain.model.NobelPrize

interface PrizeRepository {

    /**
     * Получить список всех Нобелевских премий
     */
    fun getAllPrizes(): List<NobelPrize>

    /**
     * Найти премию по году и категории
     * @param year год присуждения
     * @param category категория премии
     * @return NobelPrize или null, если не найдено
     */
    fun findPrize(year: String, category: String): NobelPrize?

    /**
     * Получить список лауреатов конкретной премии
     * @param year год присуждения
     * @param category категория премии
     * @return список лауреатов или пустой список
     */
    fun getLaureates(year: String, category: String): List<com.example.domain.model.Laureate> {
        return findPrize(year, category)?.laureates ?: emptyList()
    }
}