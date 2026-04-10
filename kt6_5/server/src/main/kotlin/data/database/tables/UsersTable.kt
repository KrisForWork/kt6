package com.example.data.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UsersTable : IntIdTable("users") {
    val username = varchar("username", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 20).default("user")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}