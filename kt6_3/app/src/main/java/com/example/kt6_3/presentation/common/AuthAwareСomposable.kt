package com.example.kt6_3.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController

@Composable
fun RequireAuth(
    sessionManager: SessionManager,
    navController: NavController,
    content: @Composable () -> Unit
) {
    val authState by sessionManager.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    when (authState) {
        is AuthState.Authenticated -> content()
        is AuthState.Unauthenticated -> {
        }
    }
}