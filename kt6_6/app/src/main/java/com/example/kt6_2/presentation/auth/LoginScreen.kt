package com.example.kt6_2.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val isLoginMode by viewModel.isLoginMode.collectAsState()
    val loginState by viewModel.loginState.collectAsState()
    val registerState by viewModel.registerState.collectAsState()

    val currentState = if (isLoginMode) loginState else registerState

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(isLoginMode) {
        viewModel.resetStates()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isLoginMode) "Login" else "Register") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isLoginMode) "Welcome back!" else "Create account",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = currentState !is AuthState.Loading
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = currentState !is AuthState.Loading
                    )

                    if (currentState is AuthState.Error) {
                        Text(
                            text = currentState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            if (isLoginMode) {
                                viewModel.login(username, password)
                            } else {
                                viewModel.register(username, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = username.isNotBlank() && password.isNotBlank() && currentState !is AuthState.Loading
                    ) {
                        if (currentState is AuthState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(if (isLoginMode) "Login" else "Register")
                        }
                    }

                    TextButton(
                        onClick = {
                            viewModel.toggleMode()
                            username = ""
                            password = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isLoginMode) "Don't have an account? Register"
                            else "Already have an account? Login"
                        )
                    }
                }
            }
        }
    }
}