package com.example.data.repository

import com.example.domain.model.Category
import com.example.domain.model.Laureate
import com.example.domain.model.NobelPrize
import com.example.domain.repository.PrizeRepository

class InMemoryPrizeRepository : PrizeRepository {

    private val prizes: MutableList<NobelPrize> = mutableListOf()

    init {
        // Заполняем тестовыми данными из Nobel Prize API
        fillTestData()
    }

    private fun fillTestData() {
        // Физика 2023
        prizes.add(
            NobelPrize(
                awardYear = "2023",
                category = Category.physics,
                dateAwarded = "2023-12-10",
                prizeAmount = 11000000,
                prizeAmountAdjusted = 11000000,
                laureates = listOf(
                    Laureate(
                        id = "1021",
                        firstname = "Pierre",
                        surname = "Agostini",
                        motivation = "\"for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter\"",
                        share = "3"
                    ),
                    Laureate(
                        id = "1022",
                        firstname = "Ferenc",
                        surname = "Krausz",
                        motivation = "\"for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter\"",
                        share = "3"
                    ),
                    Laureate(
                        id = "1023",
                        firstname = "Anne",
                        surname = "L'Huillier",
                        motivation = "\"for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter\"",
                        share = "3"
                    )
                )
            )
        )

        // Химия 2022
        prizes.add(
            NobelPrize(
                awardYear = "2022",
                category = Category.chemistry,
                dateAwarded = "2022-12-10",
                prizeAmount = 10000000,
                prizeAmountAdjusted = 10000000,
                laureates = listOf(
                    Laureate(
                        id = "1018",
                        firstname = "Carolyn",
                        surname = "Bertozzi",
                        motivation = "\"for the development of click chemistry and bioorthogonal chemistry\"",
                        share = "3"
                    ),
                    Laureate(
                        id = "1019",
                        firstname = "Morten",
                        surname = "Meldal",
                        motivation = "\"for the development of click chemistry and bioorthogonal chemistry\"",
                        share = "3"
                    ),
                    Laureate(
                        id = "1020",
                        firstname = "K. Barry",
                        surname = "Sharpless",
                        motivation = "\"for the development of click chemistry and bioorthogonal chemistry\"",
                        share = "3"
                    )
                )
            )
        )

        // Медицина 2023
        prizes.add(
            NobelPrize(
                awardYear = "2023",
                category = Category.medicine,
                dateAwarded = "2023-12-10",
                prizeAmount = 11000000,
                prizeAmountAdjusted = 11000000,
                laureates = listOf(
                    Laureate(
                        id = "1024",
                        firstname = "Katalin",
                        surname = "Karikó",
                        motivation = "\"for their discoveries concerning nucleoside base modifications that enabled the development of effective mRNA vaccines against COVID-19\"",
                        share = "2"
                    ),
                    Laureate(
                        id = "1025",
                        firstname = "Drew",
                        surname = "Weissman",
                        motivation = "\"for their discoveries concerning nucleoside base modifications that enabled the development of effective mRNA vaccines against COVID-19\"",
                        share = "2"
                    )
                )
            )
        )

        // Литература 2023
        prizes.add(
            NobelPrize(
                awardYear = "2023",
                category = Category.literature,
                dateAwarded = "2023-12-10",
                prizeAmount = 11000000,
                prizeAmountAdjusted = 11000000,
                laureates = listOf(
                    Laureate(
                        id = "1026",
                        firstname = "Jon",
                        surname = "Fosse",
                        motivation = "\"for his innovative plays and prose which give voice to the unsayable\"",
                        share = "1"
                    )
                )
            )
        )

        // Премия мира 2023
        prizes.add(
            NobelPrize(
                awardYear = "2023",
                category = Category.peace,
                dateAwarded = "2023-12-10",
                prizeAmount = 11000000,
                prizeAmountAdjusted = 11000000,
                laureates = listOf(
                    Laureate(
                        id = "1027",
                        firstname = "Narges",
                        surname = "Mohammadi",
                        motivation = "\"for her fight against the oppression of women in Iran and her fight to promote human rights and freedom for all\"",
                        share = "1"
                    )
                )
            )
        )

        // Экономика 2022
        prizes.add(
            NobelPrize(
                awardYear = "2022",
                category = Category.economics,
                dateAwarded = "2022-12-10",
                prizeAmount = 10000000,
                prizeAmountAdjusted = 10000000,
                laureates = listOf(
                    Laureate(
                        id = "1015",
                        firstname = "Ben",
                        surname = "Bernanke",
                        motivation = "\"for research on banks and financial crises\"",
                        share = "3"
                    ),
                    Laureate(
                        id = "1016",
                        firstname = "Douglas",
                        surname = "Diamond",
                        motivation = "\"for research on banks and financial crises\"",
                        share = "3"
                    ),
                    Laureate(
                        id = "1017",
                        firstname = "Philip",
                        surname = "Dybvig",
                        motivation = "\"for research on banks and financial crises\"",
                        share = "3"
                    )
                )
            )
        )

        // Физика 2022
        prizes.add(
            NobelPrize(
                awardYear = "2022",
                category = Category.physics,
                dateAwarded = "2022-12-10",
                prizeAmount = 10000000,
                prizeAmountAdjusted = 10000000,
                laureates = listOf(
                    Laureate(
                        id = "1011",
                        firstname = "Alain",
                        surname = "Aspect",
                        motivation = "\"for experiments with entangled photons, establishing the violation of Bell inequalities and pioneering quantum information science\"",
                        share = "3"
                    ),
                    Laureate(
                        id = "1012",
                        firstname = "John F.",
                        surname = "Clauser",
                        motivation = "\"for experiments with entangled photons, establishing the violation of Bell inequalities and pioneering quantum information science\"",
                        share = "3"
                    ),
                    Laureate(
                        id = "1013",
                        firstname = "Anton",
                        surname = "Zeilinger",
                        motivation = "\"for experiments with entangled photons, establishing the violation of Bell inequalities and pioneering quantum information science\"",
                        share = "3"
                    )
                )
            )
        )
    }

    override fun getAllPrizes(): List<NobelPrize> {
        return prizes.toList()  // Возвращаем неизменяемую копию
    }

    override fun findPrize(year: String, category: String): NobelPrize? {
        val categoryEnum = Category.fromString(category) ?: return null
        return prizes.find {
            it.awardYear == year && it.category == categoryEnum
        }
    }
}