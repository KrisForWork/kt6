package com.example.data.repository

import com.example.data.database.dbQuery
import com.example.data.database.tables.LaureatesTable
import com.example.data.database.tables.PrizesTable
import com.example.domain.model.Laureate
import com.example.domain.model.NobelPrize
import com.example.domain.repository.PrizeRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll

class ExposedPrizeRepository : PrizeRepository {

    private fun rowToPrize(row: ResultRow): NobelPrize {
        return NobelPrize(
            id = row[PrizesTable.id].value,  // ← .value для IntIdTable
            awardYear = row[PrizesTable.awardYear],
            category = row[PrizesTable.category],
            fullName = row[PrizesTable.fullName],
            motivation = row[PrizesTable.motivation],
            detailLink = row[PrizesTable.detailLink],
            laureates = emptyList()
        )
    }

    private fun rowToLaureate(row: ResultRow): Laureate {
        val fullName = row[LaureatesTable.fullName]
        val nameParts = fullName.split(" ")

        return Laureate(
            id = row[LaureatesTable.id].toString(),  // ← Обычный Table, можно без .value
            firstname = nameParts.firstOrNull() ?: "",
            surname = nameParts.drop(1).joinToString(" ").ifEmpty { null },
            fullName = fullName,
            motivation = row[LaureatesTable.motivation] ?: "",
            portion = row[LaureatesTable.portion],
            portraitUrl = row[LaureatesTable.portraitUrl]
        )
    }

    override suspend fun getAllPrizes(): List<NobelPrize> = dbQuery {
        PrizesTable
            .selectAll()
            .orderBy(PrizesTable.awardYear to SortOrder.DESC, PrizesTable.category to SortOrder.ASC)
            .map { rowToPrize(it) }
    }

    override suspend fun findPrizeById(id: Int): NobelPrize? = dbQuery {
        PrizesTable
            .selectAll()
            .where { PrizesTable.id eq id }
            .map { rowToPrize(it) }
            .singleOrNull()
    }

    override suspend fun findPrizeByYearAndCategory(year: String, category: String): NobelPrize? = dbQuery {
        PrizesTable
            .selectAll()
            .where { (PrizesTable.awardYear eq year) and (PrizesTable.category.lowerCase() eq category.lowercase()) }
            .map { rowToPrize(it) }
            .singleOrNull()
    }

    override suspend fun savePrize(prize: NobelPrize): NobelPrize? = dbQuery {
        val existing = findPrizeByYearAndCategory(prize.awardYear, prize.category)

        if (existing?.id != null) {
            PrizesTable.update({ PrizesTable.id eq existing.id }) {
                it[awardYear] = prize.awardYear
                it[category] = prize.category.lowercase()
                it[fullName] = prize.fullName
                it[motivation] = prize.motivation
                it[detailLink] = prize.detailLink
            }

            LaureatesTable.deleteWhere { LaureatesTable.prizeId eq existing.id }
            prize.laureates.forEach { laureate ->
                saveLaureate(existing.id, laureate)
            }

            findPrizeById(existing.id)
        } else {
            val insertStatement = PrizesTable.insert {
                it[awardYear] = prize.awardYear
                it[category] = prize.category.lowercase()
                it[fullName] = prize.fullName
                it[motivation] = prize.motivation
                it[detailLink] = prize.detailLink
            }

            val newId = insertStatement[PrizesTable.id].value  // ← .value
            prize.laureates.forEach { laureate ->
                saveLaureate(newId, laureate)
            }

            findPrizeById(newId)
        }
    }

    private suspend fun saveLaureate(prizeId: Int, laureate: Laureate) {
        LaureatesTable.insert {
            it[LaureatesTable.prizeId] = prizeId
            it[LaureatesTable.fullName] = laureate.fullName ?: "${laureate.firstname} ${laureate.surname ?: ""}".trim()
            it[LaureatesTable.portion] = laureate.portion ?: laureate.share
            it[LaureatesTable.motivation] = laureate.motivation
            it[LaureatesTable.portraitUrl] = laureate.portraitUrl
        }
    }

    override suspend fun savePrizes(prizes: List<NobelPrize>): Int = dbQuery {
        prizes.count { prize ->
            savePrize(prize) != null
        }
    }

    override suspend fun getLaureates(prizeId: Int): List<Laureate> = dbQuery {
        LaureatesTable
            .selectAll().where { LaureatesTable.prizeId eq prizeId }
            .map { rowToLaureate(it) }
    }

    override suspend fun searchPrizes(category: String?, year: String?): List<NobelPrize> = dbQuery {
        PrizesTable
            .selectAll().where {
                val categoryCondition1 =
                    category?.let { PrizesTable.category.lowerCase() eq it.lowercase() } ?: Op.TRUE
                val yearCondition1 = year?.let { PrizesTable.awardYear eq it } ?: Op.TRUE
                categoryCondition1 and yearCondition1
            }
            .orderBy(PrizesTable.awardYear to SortOrder.DESC)
            .map { rowToPrize(it) }
    }

    override suspend fun getLaureatesByYearAndCategory(year: String, category: String): List<Laureate> = dbQuery {
        val prize = findPrizeByYearAndCategory(year, category)
        if (prize?.id != null) {
            getLaureates(prize.id)
        } else {
            emptyList()
        }
    }
}