package com.example.kt6_3.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.kt6_3.presentation.common.UiState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel
) {
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val loginState by viewModel.loginState.collectAsState()
    val isLoginEnabled by viewModel.isLoginEnabled.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Только показываем ошибки, не выполняем навигацию
    LaunchedEffect(loginState) {
        if (loginState is UiState.Error) {
            snackbarHostState.showSnackbar(
                message = (loginState as UiState.Error).error.getUserMessage()
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Авторизация",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = viewModel::onUsernameChanged,
                            label = { Text("Логин") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = loginState !is UiState.Loading
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = viewModel::onPasswordChanged,
                            label = { Text("Пароль") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = loginState !is UiState.Loading
                        )

                        Button(
                            onClick = { viewModel.login() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = isLoginEnabled && loginState !is UiState.Loading
                        ) {
                            if (loginState is UiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Войти")
                            }
                        }
                    }
                }

                // Тестовые данные для быстрого ввода
                Text(
                    text = "Тестовые данные: emilys / emilyspass",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

private fun com.example.kt6_3.domain.model.AppError.getUserMessage(): String {
    return when (this) {
        is com.example.kt6_3.domain.model.AppError.NetworkError ->
            "Отсутствует подключение к интернету"
        is com.example.kt6_3.domain.model.AppError.Unauthorized ->
            "Ошибка авторизации. Попробуйте войти снова"
        is com.example.kt6_3.domain.model.AppError.NotFound ->
            "Данные не найдены"
        is com.example.kt6_3.domain.model.AppError.ValidationError ->
            message
        is com.example.kt6_3.domain.model.AppError.ServerError ->
            "Ошибка сервера (код $code): $message"
        is com.example.kt6_3.domain.model.AppError.UnknownError ->
            message
    }
}