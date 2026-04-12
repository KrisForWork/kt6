package com.example.data.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object PrizesTable : IntIdTable("prizes") {
    val awardYear = varchar("award_year", 10)
    val category = varchar("category", 50)
    val fullName = varchar("full_name", 500).nullable()
    val motivation = text("motivation").nullable()
    val detailLink = varchar("detail_link", 500).nullable()
    val prizeAmount = long("prize_amount").nullable()
    val prizeAmountAdjusted = long("prize_amount_adjusted").nullable()
    val dateAwarded = varchar("date_awarded", 50).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    init {
        uniqueIndex("idx_prize_year_category", awardYear, category)
    }
}