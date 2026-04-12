// app/src/main/java/com/example/kt6_2/MainActivity.kt
package com.example.kt6_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.kt6_2.data.auth.TokenManager
import com.example.kt6_2.di.AppModule
import com.example.kt6_2.navigation.NavGraph
import com.example.kt6_2.presentation.auth.AuthViewModel
import com.example.kt6_2.presentation.favorites.FavoritesViewModel
import com.example.kt6_2.presentation.list.LaureatesListViewModel
import com.example.kt6_2.ui.theme.Kt6_2Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppModule.init(applicationContext)

        val tokenManager = TokenManager(applicationContext)
        tokenManager.clearTokenIfExists()

        setContent {
            Kt6_2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val listViewModel: LaureatesListViewModel = viewModel(
                        factory = AppModule.provideLaureatesListViewModelFactory()
                    )

                    val authViewModel: AuthViewModel = viewModel(
                        factory = AppModule.provideAuthViewModelFactory()
                    )

                    val favoritesViewModel: FavoritesViewModel = viewModel(
                        factory = AppModule.provideFavoritesViewModelFactory()
                    )

                    val getCurrentUserUseCase = AppModule.provideGetCurrentUserUseCase()

                    NavGraph(
                        navController = navController,
                        listViewModel = listViewModel,
                        authViewModel = authViewModel,
                        favoritesViewModel = favoritesViewModel,
                        getCurrentUserUseCase = getCurrentUserUseCase
                    )
                }
            }
        }
    }
}