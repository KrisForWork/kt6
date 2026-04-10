package com.example.data.database

object DatabaseConfig {
    private const val HOST = "ep-morning-silence-aleiq9s0.c-3.eu-central-1.aws.neon.tech"
    private const val DATABASE = "neondb"
    private const val USER = "neondb_owner"
    private const val PASSWORD = "npg_Bq3oZabjms6T"

    const val URL = "jdbc:postgresql://$HOST/$DATABASE?sslmode=require"
    const val DRIVER = "org.postgresql.Driver"

    const val USERNAME = USER
    const val PASSWORD_VALUE = PASSWORD
    const val MAX_POOL_SIZE = 10
    const val MIN_IDLE = 2
    const val IDLE_TIMEOUT_MS = 300000L
    const val MAX_LIFETIME_MS = 1800000L
    const val CONNECTION_TIMEOUT_MS = 30000L

    fun getJdbcUrl(): String = URL
    fun getUser(): String = USER
    fun getPassword(): String = PASSWORD
}