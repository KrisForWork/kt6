package com.example

import com.example.data.database.DatabaseFactory
import com.example.data.repository.ExposedPrizeRepository
import com.example.data.repository.ExposedUserRepository
import com.example.domain.model.User
import kotlinx.coroutines.runBlocking

/**
 * Тестовый файл для отладки работы с БД
 */
fun main() {
    println("=== ТЕСТИРОВАНИЕ БАЗЫ ДАННЫХ ===\n")

    // Инициализация БД
    println("1. Подключение к БД...")
    DatabaseFactory.init()
    println("✅ БД подключена\n")

    runBlocking {
        val userRepository = ExposedUserRepository()
        val prizeRepository = ExposedPrizeRepository()

        // ТЕСТ 1: Проверка таблицы prizes
        println("2. Проверка таблицы prizes:")
        val allPrizes = prizeRepository.getAllPrizes()
        println("   Найдено премий: ${allPrizes.size}")

        if (allPrizes.isNotEmpty()) {
            println("   Первые 3 премии:")
            allPrizes.take(3).forEach { prize ->
                println("   - ID: ${prize.id}, Year: ${prize.awardYear}, Category: ${prize.category}")
            }
        } else {
            println("   ❌ Таблица prizes ПУСТА!")
        }
        println()

        // ТЕСТ 2: Поиск премии по ID = 1
        println("3. Поиск премии с ID = 1:")
        val prize1 = prizeRepository.findPrizeById(1)
        if (prize1 != null) {
            println("   ✅ Найдена: Year=${prize1.awardYear}, Category=${prize1.category}")
        } else {
            println("   ❌ Премия с ID=1 НЕ НАЙДЕНА!")
        }
        println()

        // ТЕСТ 3: Поиск премии по ID = 2
        println("4. Поиск премии с ID = 2:")
        val prize2 = prizeRepository.findPrizeById(2)
        if (prize2 != null) {
            println("   ✅ Найдена: Year=${prize2.awardYear}, Category=${prize2.category}")
        } else {
            println("   ❌ Премия с ID=2 НЕ НАЙДЕНА!")
        }
        println()

        // ТЕСТ 4: Проверка всех ID в таблице
        println("5. Все ID в таблице prizes:")
        val allIds = allPrizes.map { it.id }
        println("   Доступные ID: $allIds")
        println()

        // ТЕСТ 5: Проверка таблицы users
        println("6. Проверка таблицы users:")
        val users = listOf("admin", "user", "test").mapNotNull { username ->
            userRepository.findByUsername(username)
        }
        println("   Найдено пользователей: ${users.size}")
        users.forEach { user ->
            println("   - ID: ${user.id}, Username: ${user.username}, Role: ${user.role}")
        }
        println()

        // ТЕСТ 6: Создание тестового пользователя
        println("7. Создание тестового пользователя:")
        val testUser = userRepository.findByUsername("testuser")
        if (testUser == null) {
            val newUser = userRepository.createUser("testuser", "test123", "user")
            if (newUser != null) {
                println("   ✅ Создан пользователь: ID=${newUser.id}, Username=${newUser.username}")
            } else {
                println("   ❌ Не удалось создать пользователя")
            }
        } else {
            println("   Пользователь 'testuser' уже существует: ID=${testUser.id}")
        }
        println()

        // ТЕСТ 7: Прямой SQL-запрос для проверки (если нужно)
        println("8. Проверка через прямой SQL (если поддерживается):")
        try {
            org.jetbrains.exposed.sql.transactions.transaction {
                val result = exec("SELECT COUNT(*) FROM prizes") { rs ->
                    rs.next()
                    rs.getInt(1)
                }
                println("   Количество записей в prizes: $result")
            }
        } catch (e: Exception) {
            println("   ⚠️ Прямой SQL не поддерживается: ${e.message}")
        }
        println()
    }

    println("=== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===")

    // Закрываем соединение
    DatabaseFactory.shutdown()
}