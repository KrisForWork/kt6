package com.example.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserPrizesTable : Table("user_prizes") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val prizeId = reference("prize_id", PrizesTable, onDelete = ReferenceOption.CASCADE)
    val addedAt = datetime("added_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(userId, prizeId, name = "pk_user_prizes")
}