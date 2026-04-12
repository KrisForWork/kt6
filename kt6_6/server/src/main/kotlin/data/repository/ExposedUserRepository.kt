package com.example.data.repository

import com.example.data.database.dbQuery
import com.example.data.database.tables.PrizesTable
import com.example.data.database.tables.UserPrizesTable
import com.example.data.database.tables.UsersTable
import com.example.domain.model.NobelPrize
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ExposedUserRepository : UserRepository {

    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[UsersTable.id].value,
            username = row[UsersTable.username],
            passwordHash = row[UsersTable.passwordHash],
            role = row[UsersTable.role],
            createdAt = row[UsersTable.createdAt].toString()
        )
    }

    private fun rowToPrize(row: ResultRow): NobelPrize {
        return NobelPrize(
            id = row[PrizesTable.id].value,
            awardYear = row[PrizesTable.awardYear],
            category = row[PrizesTable.category],
            fullName = row[PrizesTable.fullName],
            motivation = row[PrizesTable.motivation],
            detailLink = row[PrizesTable.detailLink],
            laureates = emptyList()
        )
    }

    override suspend fun findByUsername(username: String): User? = dbQuery {
        UsersTable
            .selectAll().where { UsersTable.username eq username }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    override suspend fun findById(id: Int): User? = dbQuery {
        UsersTable
            .selectAll().where { UsersTable.id eq id }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    override suspend fun createUser(username: String, passwordHash: String, role: String): User? = dbQuery {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

        val insertStatement = UsersTable.insert {
            it[UsersTable.username] = username
            it[UsersTable.passwordHash] = passwordHash
            it[UsersTable.role] = role
            it[UsersTable.createdAt] = now
        }

        insertStatement.resultedValues?.singleOrNull()?.let { rowToUser(it) }
    }

    override suspend fun getUserFavorites(userId: Int): List<NobelPrize> = dbQuery {
        (PrizesTable innerJoin UserPrizesTable)
            .selectAll().where { UserPrizesTable.userId eq userId }
            .map { rowToPrize(it) }
    }

    override suspend fun getFavoriteIds(userId: Int): List<Int> = dbQuery {
        UserPrizesTable
            .selectAll().where { UserPrizesTable.userId eq userId }
            .map { it[UserPrizesTable.prizeId].value }
    }

    override suspend fun addFavorite(userId: Int, prizeId: Int): Boolean = dbQuery {
        val exists = UserPrizesTable
            .selectAll()
            .where { (UserPrizesTable.userId eq userId) and (UserPrizesTable.prizeId eq prizeId) }
            .empty()
            .not()

        if (!exists) {
            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            UserPrizesTable.insert {
                it[UserPrizesTable.userId] = userId
                it[UserPrizesTable.prizeId] = prizeId
                it[UserPrizesTable.addedAt] = now
            }
        }
        true
    }

    override suspend fun removeFavorite(userId: Int, prizeId: Int): Boolean = dbQuery {
        UserPrizesTable
            .deleteWhere { (UserPrizesTable.userId eq userId) and (UserPrizesTable.prizeId eq prizeId) } > 0
    }

    override suspend fun isFavorite(userId: Int, prizeId: Int): Boolean = dbQuery {
        UserPrizesTable
            .selectAll()
            .where { (UserPrizesTable.userId eq userId) and (UserPrizesTable.prizeId eq prizeId) }
            .empty()
            .not()
    }
}