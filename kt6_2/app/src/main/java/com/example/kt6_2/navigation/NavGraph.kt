package com.example.kt6_2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.kt6_2.domain.model.Laureate
import com.example.kt6_2.domain.model.NobelPrize
import com.example.kt6_2.presentation.detail.LaureateDetailScreen
import com.example.kt6_2.presentation.list.LaureatesListScreen
import com.example.kt6_2.presentation.list.LaureatesListViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    listViewModel: LaureatesListViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.List.route,
        modifier = modifier
    ) {
        composable(Screen.List.route) {
            LaureatesListScreen(
                viewModel = listViewModel,
                onLaureateClick = { prize: NobelPrize, laureate: Laureate ->
                    val data = DetailScreenData(prize, laureate)
                    navController.navigate(Screen.Detail.createRoute(data))
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
                LaureateDetailScreen(
                    prize = detailData.prize,
                    laureate = detailData.laureate,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            } else {
                navController.popBackStack()
            }
        }
    }
}