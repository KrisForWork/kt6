package com.example.domain.repository

import com.example.domain.model.Laureate
import com.example.domain.model.NobelPrize

interface PrizeRepository {

    suspend fun getAllPrizes(): List<NobelPrize>

    suspend fun findPrizeById(id: Int): NobelPrize?

    suspend fun findPrizeByYearAndCategory(year: String, category: String): NobelPrize?

    suspend fun savePrize(prize: NobelPrize): NobelPrize?

    suspend fun savePrizes(prizes: List<NobelPrize>): Int

    suspend fun getLaureates(prizeId: Int): List<Laureate>

    suspend fun getLaureatesByYearAndCategory(year: String, category: String): List<Laureate> {
        val prize = findPrizeByYearAndCategory(year, category)
        return if (prize?.id != null) getLaureates(prize.id) else emptyList()
    }

    suspend fun searchPrizes(category: String? = null, year: String? = null): List<NobelPrize>
}