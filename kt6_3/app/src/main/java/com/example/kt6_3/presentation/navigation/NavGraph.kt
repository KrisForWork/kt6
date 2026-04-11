package com.example.kt6_3.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.kt6_3.presentation.common.SessionManager
import com.example.kt6_3.presentation.common.UiState
import com.example.kt6_3.presentation.login.LoginScreen
import com.example.kt6_3.presentation.userdetail.UserDetailScreen
import com.example.kt6_3.presentation.users.UsersListScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            val viewModel: com.example.kt6_3.presentation.login.LoginViewModel = koinViewModel()

            LoginScreen(
                viewModel = viewModel
            )
        }

        composable(Screen.UsersList.route) {
            UsersListScreen(
                viewModel = koinViewModel(),
                onUserClick = { userId ->
                    navController.navigate(Screen.UserDetail.createRoute(userId))
                },
                onLogout = {
                    sessionManager.logout()
                }
            )
        }

        composable(
            route = Screen.UserDetail.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: return@composable

            UserDetailScreen(
                viewModel = koinViewModel(
                    parameters = { parametersOf(userId) }
                ),
                onBackClick = {
                    navController.popBackStack()
                },
                onLogout = {
                    sessionManager.logout()
                }
            )
        }
    }
}