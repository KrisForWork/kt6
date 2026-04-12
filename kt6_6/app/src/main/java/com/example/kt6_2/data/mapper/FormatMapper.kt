package com.example.kt6_2.data.mapper

import com.example.kt6_2.domain.model.Laureate
import com.example.kt6_2.domain.model.NobelPrize
import java.text.NumberFormat
import java.util.Locale

object FormatMapper {

    private val currencyFormatter = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }

    fun NobelPrize.formatPrizeAmount(): String? {
        return prizeAmount?.let { amount ->
            "${currencyFormatter.format(amount)} SEK"
        }
    }

    fun NobelPrize.formatAdjustedPrizeAmount(): String? {
        return prizeAmountAdjusted?.let { amount ->
            "${currencyFormatter.format(amount)} SEK (adjusted)"
        }
    }

    fun Laureate.getShareDescription(): String {
        return when (portion) {
            "1" -> "Full prize"
            "2" -> "1/2 of the prize"
            "3" -> "1/3 of the prize"
            "4" -> "1/4 of the prize"
            else -> portion?.let { "Share: $it" } ?: ""
        }
    }

    fun NobelPrize.getDisplayTitle(): String {
        return buildString {
            append(category.replaceFirstChar { it.uppercase() })
            fullName?.let { append(" - $it") }
            append(" (${awardYear})")
        }
    }
}