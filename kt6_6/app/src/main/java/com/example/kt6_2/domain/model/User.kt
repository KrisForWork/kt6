// app/src/main/java/com/example/kt6_2/domain/model/User.kt
package com.example.kt6_2.domain.model

data class User(
    val id: Int? = null,
    val username: String,
    val role: String = "user"
)