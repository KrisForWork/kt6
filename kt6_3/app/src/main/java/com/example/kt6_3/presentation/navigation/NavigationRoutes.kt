package com.example.kt6_3.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object UsersList : Screen("users_list")
    data object UserDetail : Screen("user_detail/{userId}") {
        fun createRoute(userId: Int) = "user_detail/$userId"
    }
}