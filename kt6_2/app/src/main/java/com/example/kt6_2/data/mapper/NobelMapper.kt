package com.example.kt6_2.data.mapper

import com.example.kt6_2.data.api.models.LaureateDto
import com.example.kt6_2.data.api.models.NobelPrizeDto
import com.example.kt6_2.domain.model.Laureate
import com.example.kt6_2.domain.model.NobelPrize

fun NobelPrizeDto.toDomain(): NobelPrize? {
    val year = awardYear ?: return null
    val category = category?.en ?: return null

    return NobelPrize(
        year = year,
        category = category,
        categoryFullName = categoryFullName?.en,
        dateAwarded = dateAwarded,
        motivation = extractMotivation(this),
        prizeAmount = prizeAmount,
        prizeAmountAdjusted = prizeAmountAdjusted,
        laureates = laureates?.mapNotNull { it.toDomain() } ?: emptyList()
    )
}

fun LaureateDto.toDomain(): Laureate? {
    val id = id ?: return null
    val fullName = fullName?.en ?: knownName?.en ?: return null

    return Laureate(
        id = id,
        fullName = fullName,
        knownName = knownName?.en,
        birthCountry = extractBirthCountry(this),
        birthPlace = extractBirthPlace(this),
        wikipediaUrl = wikipedia?.english?.let { "https://en.wikipedia.org/wiki/$it" }
    )
}

private fun extractMotivation(dto: NobelPrizeDto): String? {
    return dto.laureates?.firstOrNull()?.motivation?.en
        ?: dto.topMotivation?.en
}

private fun extractBirthCountry(dto: LaureateDto): String? {
    return dto.birth?.place?.country?.en
        ?: dto.birth?.place?.countryNow?.en
}

private fun extractBirthPlace(dto: LaureateDto): String? {
    return dto.birth?.place?.city?.en
        ?: dto.birth?.place?.cityNow?.en
        ?: dto.birth?.place?.locationString?.en
}

private fun extractPortraitUrl(laureateId: String): String? {
    return "https://www.nobelprize.org/images/$laureateId-portrait-medium.jpg"
}

fun List<NobelPrizeDto>.toDomainList(): List<NobelPrize> {
    return this.mapNotNull { it.toDomain() }
        .filter { it.laureates.isNotEmpty() }
}