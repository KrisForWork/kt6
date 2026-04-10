package com.example.security

/**
 * Простой тест для проверки PasswordHasher
 * Запусти main() для проверки
 */
fun main() {
    println("=== Тестирование PasswordHasher ===\n")

    val password = "mySecret123"
    val wrongPassword = "wrongPassword"

    // Тест 1: Хеширование пароля
    println("Тест 1: Хеширование пароля")
    val hash = PasswordHasher.hash(password)
    println("✅ Пароль: $password")
    println("✅ Хеш: $hash")
    println("✅ Длина хеша: ${hash.length} символов\n")

    // Тест 2: Проверка правильного пароля
    println("Тест 2: Проверка правильного пароля")
    val verifyCorrect = PasswordHasher.verify(password, hash)
    if (verifyCorrect) {
        println("✅ Пароль '$password' ВЕРНО соответствует хешу")
    } else {
        println("❌ ОШИБКА: Пароль '$password' НЕ соответствует хешу")
    }
    println()

    // Тест 3: Проверка неправильного пароля
    println("Тест 3: Проверка неправильного пароля")
    val verifyWrong = PasswordHasher.verify(wrongPassword, hash)
    if (!verifyWrong) {
        println("✅ Пароль '$wrongPassword' ВЕРНО НЕ соответствует хешу")
    } else {
        println("❌ ОШИБКА: Пароль '$wrongPassword' соответствует хешу")
    }
    println()

    // Тест 4: Разные хеши для одного пароля
    println("Тест 4: Разные хеши для одного пароля (соль)")
    val hash2 = PasswordHasher.hash(password)
    val hash3 = PasswordHasher.hash(password)
    if (hash != hash2 && hash2 != hash3 && hash != hash3) {
        println("✅ Все три хеша разные (соль работает корректно)")
        println("   Хеш 1: ${hash.take(30)}...")
        println("   Хеш 2: ${hash2.take(30)}...")
        println("   Хеш 3: ${hash3.take(30)}...")
    } else {
        println("❌ ОШИБКА: Хеши совпадают (соль не работает)")
    }
    println()

    // Тест 5: Проверка всех трёх хешей
    println("Тест 5: Проверка всех трёх хешей с правильным паролем")
    val allValid = listOf(hash, hash2, hash3).all { PasswordHasher.verify(password, it) }
    if (allValid) {
        println("✅ Все три хеша валидны для пароля '$password'")
    } else {
        println("❌ ОШИБКА: Не все хеши валидны")
    }
    println()

    // Тест 6: Проверка needsRehash
    println("Тест 6: Проверка needsRehash")
    val needsRehash = PasswordHasher.needsRehash(hash)
    if (!needsRehash) {
        println("✅ needsRehash = false (хеш актуален)")
    } else {
        println("⚠️ needsRehash = true (хеш требует обновления)")
    }
    println()

    // Тест 7: Пустой пароль
    println("Тест 7: Пустой пароль")
    val emptyHash = PasswordHasher.hash("")
    val emptyVerify = PasswordHasher.verify("", emptyHash)
    if (emptyVerify) {
        println("✅ Пустой пароль корректно хешируется и проверяется")
    } else {
        println("❌ ОШИБКА: Пустой пароль не работает")
    }
    println()

    // Тест 8: Длинный пароль
    println("Тест 8: Длинный пароль (100 символов)")
    val longPassword = "a".repeat(100)
    val longHash = PasswordHasher.hash(longPassword)
    val longVerify = PasswordHasher.verify(longPassword, longHash)
    if (longVerify) {
        println("✅ Длинный пароль корректно обработан")
        println("   Длина хеша: ${longHash.length} символов")
    } else {
        println("❌ ОШИБКА: Длинный пароль не работает")
    }
    println()

    // Тест 9: Специальные символы
    println("Тест 9: Специальные символы и эмодзи")
    val specialPassword = "P@$! 🚀🔥 Kotlin"
    val specialHash = PasswordHasher.hash(specialPassword)
    val specialVerify = PasswordHasher.verify(specialPassword, specialHash)
    if (specialVerify) {
        println("✅ Пароль со спецсимволами и эмодзи работает корректно")
        println("   Пароль: $specialPassword")
    } else {
        println("❌ ОШИБКА: Пароль со спецсимволами не работает")
    }
    println()

    // Тест 10: Неверный формат хеша
    println("Тест 10: Проверка неверного формата хеша")
    val invalidHash = "invalid_hash_format"
    val invalidVerify = PasswordHasher.verify(password, invalidHash)
    if (!invalidVerify) {
        println("✅ Неверный формат хеша корректно отклонён")
    } else {
        println("❌ ОШИБКА: Неверный формат хеша принят как валидный")
    }
    println()

    // Итоги
    println("=== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===")
    println("\nPasswordHasher готов к использованию в проекте!")
}