package com.example.kt6_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.kt6_3.presentation.common.AuthState
import com.example.kt6_3.presentation.common.SessionManager
import com.example.kt6_3.presentation.navigation.AppNavGraph
import com.example.kt6_3.presentation.navigation.Screen
import com.example.kt6_3.ui.theme.Kt6_3Theme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Kt6_3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        val authState by sessionManager.authState.collectAsState()

        LaunchedEffect(authState) {
            when (authState) {
                is AuthState.Authenticated -> {
                    val currentRoute = navController.currentDestination?.route
                    if (currentRoute == Screen.Login.route || currentRoute == null) {
                        navController.navigate(Screen.UsersList.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
                is AuthState.Unauthenticated -> {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }

        AppNavGraph(
            navController = navController,
            sessionManager = sessionManager
        )
    }
}