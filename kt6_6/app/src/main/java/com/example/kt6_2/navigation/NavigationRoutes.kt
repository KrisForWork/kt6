// app/src/main/java/com/example/kt6_2/navigation/NavigationRoutes.kt
package com.example.kt6_2.navigation

sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object List : Screen("list")
    data object Favorites : Screen("favorites")
    data object Detail : Screen("detail/{data}") {
        fun createRoute(data: DetailScreenData): String {
            return "detail/${data.encodeToJson()}"
        }
    }
}