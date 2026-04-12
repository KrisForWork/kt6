package com.example.kt6_2.data.mapper

import android.util.Log
import com.example.kt6_2.data.api.models.LaureateDto
import com.example.kt6_2.data.api.models.NobelPrizeDto
import com.example.kt6_2.domain.model.Laureate
import com.example.kt6_2.domain.model.NobelPrize

fun NobelPrizeDto.toDomain(): NobelPrize {
    Log.d("NobelMapper", "toDomain: ${this.awardYear} ${this.category} - ${this.laureates.size} laureates")
    return NobelPrize(
        id = id,
        awardYear = awardYear ?: "",
        category = category ?: "",
        fullName = fullName,
        motivation = motivation,
        prizeAmount = prizeAmount,
        prizeAmountAdjusted = prizeAmountAdjusted,
        dateAwarded = dateAwarded,
        detailLink = detailLink,
        laureates = laureates.mapNotNull { it.toDomain() }
    )
}

fun LaureateDto.toDomain(): Laureate {
    val computedFullName = fullName ?: buildString {
        append(firstname)
        surname?.let { append(" $it") }
    }

    return Laureate(
        id = id,
        firstname = firstname,
        surname = surname,
        fullName = computedFullName,
        motivation = motivation,
        portion = portion,
        portraitUrl = portraitUrl
    )
}

fun Laureate.toDto(): LaureateDto {
    return LaureateDto(
        id = id,
        firstname = firstname,
        surname = surname,
        fullName = fullName,
        motivation = motivation,
        portion = portion,
        portraitUrl = portraitUrl
    )
}

fun List<NobelPrizeDto>.toDomainList(): List<NobelPrize> {
    Log.d("NobelMapper", "toDomainList: input size = ${this.size}")
    return this.map { it.toDomain() }
        .filter {
            val keep = it.laureates.isNotEmpty()
            if (!keep) {
                Log.d("NobelMapper", "Filtered out ${it.awardYear} ${it.category} - no laureates")
            }
            keep
        }
        .also { Log.d("NobelMapper", "toDomainList: output size = ${it.size}") }
}

fun NobelPrize.toDto(): NobelPrizeDto {
    return NobelPrizeDto(
        id = id,
        awardYear = awardYear,
        category = category,
        fullName = fullName,
        motivation = motivation,
        prizeAmount = prizeAmount,
        prizeAmountAdjusted = prizeAmountAdjusted,
        dateAwarded = dateAwarded,
        detailLink = detailLink,
        laureates = laureates.map { it.toDto() }
    )
}
