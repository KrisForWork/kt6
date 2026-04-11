package com.example.kt6_3.presentation.userdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kt6_3.presentation.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    viewModel: UserDetailViewModel,
    onBackClick: () -> Unit,
    onLogout: () -> Unit
) {
    val userState by viewModel.userState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Обработка ошибок
    LaunchedEffect(userState) {
        if (userState is UiState.Error) {
            snackbarHostState.showSnackbar(
                message = (userState as UiState.Error).error.getUserMessage()
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (val state = userState) {
                        is UiState.Success -> Text("${state.data.firstName} ${state.data.lastName}")
                        else -> Text("Детали пользователя")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Выйти"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = userState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is UiState.Success -> {
                    UserDetailContent(user = state.data)
                }

                is UiState.Error -> {
                    ErrorContent(
                        error = state.error,
                        onRetry = { viewModel.retry() }
                    )
                }
            }
        }
    }
}

@Composable
private fun UserDetailContent(
    user: com.example.kt6_3.domain.model.User
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Большой аватар
                AsyncImage(
                    model = user.image,
                    contentDescription = "Аватар ${user.firstName}",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                // Имя и фамилия
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                // Username
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Информационные поля
                InfoRow(label = "ID", value = user.id.toString())
                InfoRow(label = "Email", value = user.email)
                InfoRow(label = "Имя", value = user.firstName)
                InfoRow(label = "Фамилия", value = user.lastName)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка выхода внизу экрана
        Button(
            onClick = {
                // Логика выхода в ViewModel
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Выйти из аккаунта")
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ErrorContent(
    error: com.example.kt6_3.domain.model.AppError,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error.getUserMessage(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Повторить")
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