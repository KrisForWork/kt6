package com.example.kt6_2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.kt6_2.domain.usecase.GetCurrentUserUseCase
import com.example.kt6_2.presentation.auth.AuthScreen
import com.example.kt6_2.presentation.auth.AuthState
import com.example.kt6_2.presentation.auth.AuthViewModel
import com.example.kt6_2.presentation.detail.LaureateDetailScreen
import com.example.kt6_2.presentation.favorites.FavoritesScreen
import com.example.kt6_2.presentation.favorites.FavoritesViewModel
import com.example.kt6_2.presentation.list.LaureatesListScreen
import com.example.kt6_2.presentation.list.LaureatesListViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    listViewModel: LaureatesListViewModel,
    authViewModel: AuthViewModel,
    favoritesViewModel: FavoritesViewModel,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    modifier: Modifier = Modifier
) {
    val startDestination = Screen.Auth.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Auth.route) {
            val loginState by authViewModel.loginState.collectAsState()
            val registerState by authViewModel.registerState.collectAsState()

            LaunchedEffect(Unit) {
                if (getCurrentUserUseCase.isLoggedIn()) {
                    android.util.Log.d("NavGraph", "Already logged in, navigating to list")
                    navController.navigate(Screen.List.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            }

            LaunchedEffect(loginState, registerState) {
                val isSuccess = loginState is AuthState.Success || registerState is AuthState.Success
                if (isSuccess) {
                    android.util.Log.d("NavGraph", "Auth success, navigating to list")
                    favoritesViewModel.loadFavorites()
                    navController.navigate(Screen.List.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            AuthScreen(viewModel = authViewModel)
        }

        composable(Screen.List.route) {
            val isLoggedIn = getCurrentUserUseCase.isLoggedIn()

            if (!isLoggedIn) {
                android.util.Log.d("NavGraph", "Not logged in, redirecting to auth")
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.List.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            } else {
                android.util.Log.d("NavGraph", "Showing list screen")

                LaunchedEffect(Unit) {
                    listViewModel.loadInitialData()
                }

                LaureatesListScreen(
                    viewModel = listViewModel,
                    onLaureateClick = { prize, laureate ->
                        val data = DetailScreenData(prize, laureate)
                        navController.navigate(Screen.Detail.createRoute(data))
                    },
                    onFavoritesClick = {
                        navController.navigate(Screen.Favorites.route)
                    },
                    onLogoutClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                viewModel = favoritesViewModel,
                onPrizeClick = { prize ->
                    prize.laureates.firstOrNull()?.let { laureate ->
                        val data = DetailScreenData(prize, laureate)
                        navController.navigate(Screen.Detail.createRoute(data))
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("data") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dataJson = backStackEntry.arguments?.getString("data")
            val detailData = dataJson?.decodeFromJson()

            if (detailData != null) {
                val prize = detailData.prize
                val isFavorite = favoritesViewModel.isFavorite(prize.id ?: 0)
                val operationInProgress by favoritesViewModel.favoriteOperationInProgress.collectAsState()
                val isFavoriteLoading = prize.id in operationInProgress

                LaureateDetailScreen(
                    prize = prize,
                    laureate = detailData.laureate,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    isFavorite = isFavorite,
                    isFavoriteLoading = isFavoriteLoading,
                    onFavoriteClick = {
                        prize.id?.let { id ->
                            if (isFavorite) {
                                favoritesViewModel.removeFavorite(id)
                            } else {
                                favoritesViewModel.addFavorite(id)
                            }
                        }
                    }
                )
            } else {
                navController.popBackStack()
            }
        }
    }
}