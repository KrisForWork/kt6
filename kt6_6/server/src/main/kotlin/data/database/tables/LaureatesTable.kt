package com.example.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object LaureatesTable : Table("laureates") {
    val id = integer("id").autoIncrement()
    val prizeId = integer("prize_id").references(PrizesTable.id, onDelete = ReferenceOption.CASCADE)
    val fullName = varchar("full_name", 200)
    val portion = varchar("portion", 10).nullable()
    val motivation = text("motivation").nullable()
    val portraitUrl = varchar("portrait_url", 500).nullable()

    override val primaryKey = PrimaryKey(id)
}