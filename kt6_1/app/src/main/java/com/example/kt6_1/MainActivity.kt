package com.example.kt6_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kt6_1.presentation.navigation.Screen
import com.example.kt6_1.presentation.navigation.ScreenTransitions
import com.example.kt6_1.presentation.ui.detail.PhotoDetailScreen
import com.example.kt6_1.presentation.ui.list.PhotoListScreen
import com.example.kt6_1.ui.theme.Kt6_1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Kt6_1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhotoCatalogApp()
                }
            }
        }
    }
}

@Composable
fun PhotoCatalogApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.PhotoList.route
    ) {
        composable(
            route = Screen.PhotoList.route,
            enterTransition = { ScreenTransitions.enterTransition() },
            exitTransition = { ScreenTransitions.exitTransition() },
            popEnterTransition = { ScreenTransitions.popEnterTransition() },
            popExitTransition = { ScreenTransitions.popExitTransition() }
        ) {
            PhotoListScreen(
                onPhotoClick = { photoId ->
                    navController.navigate(Screen.PhotoDetail.createRoute(photoId))
                }
            )
        }

        composable(
            route = Screen.PhotoDetail.route,
            arguments = listOf(
                navArgument("photoId") { type = NavType.StringType }
            ),
            enterTransition = { ScreenTransitions.enterTransition() },
            exitTransition = { ScreenTransitions.exitTransition() },
            popEnterTransition = { ScreenTransitions.popEnterTransition() },
            popExitTransition = { ScreenTransitions.popExitTransition() }
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getString("photoId") ?: return@composable
            PhotoDetailScreen(
                photoId = photoId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}