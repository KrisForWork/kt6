package com.example.kt6_2.navigation

sealed class Screen(val route: String) {
    data object List : Screen("list")
    data object Detail : Screen("detail/{data}") {
        fun createRoute(data: DetailScreenData): String {
            return "detail/${data.encodeToJson()}"
        }
    }
}