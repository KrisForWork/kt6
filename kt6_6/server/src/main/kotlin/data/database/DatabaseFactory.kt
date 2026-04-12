package com.example.data.database

import com.example.data.database.tables.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    private lateinit var dataSource: HikariDataSource

    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = DatabaseConfig.URL
            username = DatabaseConfig.getUser()
            password = DatabaseConfig.getPassword()
            driverClassName = DatabaseConfig.DRIVER
            maximumPoolSize = DatabaseConfig.MAX_POOL_SIZE
            minimumIdle = DatabaseConfig.MIN_IDLE
            idleTimeout = DatabaseConfig.IDLE_TIMEOUT_MS
            maxLifetime = DatabaseConfig.MAX_LIFETIME_MS
            connectionTimeout = DatabaseConfig.CONNECTION_TIMEOUT_MS
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }

        dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(
                UsersTable,
                PrizesTable,
                LaureatesTable,
                UserPrizesTable
            )
        }

        println("Database connected and tables created!")
    }

    fun shutdown() {
        dataSource.close()
        println("Database connection closed")
    }
}

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }