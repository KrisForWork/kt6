package com.example.kt6_2.data.api.models

import com.example.kt6_2.data.api.serializer.LinksDeserializer
import kotlinx.serialization.Serializable

@Serializable
data class NobelPrizesResponse(
    val nobelPrizes: List<NobelPrizeDto> = emptyList(),
    val meta: MetaDto? = null,
    val links: LinksDto? = null
)

@Serializable
data class NobelPrizeDto(
    val awardYear: String? = null,
    val category: CategoryDto? = null,
    val categoryFullName: LocalizedStringDto? = null,
    val dateAwarded: String? = null,
    val prizeAmount: Int? = null,
    val prizeAmountAdjusted: Int? = null,
    val laureates: List<LaureateDto>? = null,
    val topMotivation: LocalizedStringDto? = null
)

@Serializable
data class CategoryDto(
    val en: String? = null,
    val no: String? = null,
    val se: String? = null
)

@Serializable
data class LaureateDto(
    val id: String? = null,
    val knownName: LocalizedStringDto? = null,
    val fullName: LocalizedStringDto? = null,
    val portion: String? = null,
    val sortOrder: String? = null,
    val motivation: LocalizedStringDto? = null,
    @Serializable(with = LinksDeserializer::class)
    val links: LinksDto? = null,
    val birth: EventDto? = null,
    val death: EventDto? = null,
    val wikipedia: WikipediaDto? = null,
    val wikidata: WikidataDto? = null
)

@Serializable
data class LocalizedStringDto(
    val en: String? = null,
    val no: String? = null,
    val se: String? = null
)

@Serializable
data class EventDto(
    val date: String? = null,
    val place: PlaceDto? = null
)

@Serializable
data class PlaceDto(
    val city: LocalizedStringDto? = null,
    val country: LocalizedStringDto? = null,
    val cityNow: LocalizedStringDto? = null,
    val countryNow: LocalizedStringDto? = null,
    val continent: LocalizedStringDto? = null,
    val locationString: LocalizedStringDto? = null
)

@Serializable
data class WikipediaDto(
    val slug: String? = null,
    val english: String? = null
)

@Serializable
data class WikidataDto(
    val id: String? = null,
    val url: String? = null
)

@Serializable
data class MetaDto(
    val offset: Int? = null,
    val limit: Int? = null,
    val count: Int? = null,
    val total: Int? = null
)

@Serializable
data class LinksDto(
    val rel: String? = null,
    val href: String? = null,
    val action: String? = null,
    val types: String? = null
)