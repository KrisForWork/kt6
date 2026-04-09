package com.example.kt6_1.presentation.navigation

sealed class Screen(val route: String) {
    data object PhotoList : Screen("photo_list")
    data object PhotoDetail : Screen("photo_detail/{photoId}") {
        fun createRoute(photoId: String) = "photo_detail/$photoId"
    }
}