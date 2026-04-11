package com.example.kt6_3.presentation.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kt6_3.domain.model.User
import com.example.kt6_3.presentation.common.UiState
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersListScreen(
    viewModel: UsersListViewModel,
    onUserClick: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val usersState by viewModel.usersState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Обработка ошибок
    LaunchedEffect(usersState) {
        if (usersState is UiState.Error) {
            snackbarHostState.showSnackbar(
                message = (usersState as UiState.Error).error.getUserMessage()
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Пользователи") },
                actions = {
                    IconButton(onClick = onLogout) {
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
            when (val state = usersState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is UiState.Success -> {
                    UsersListContent(
                        users = state.data,
                        isRefreshing = isRefreshing,
                        onRefresh = { viewModel.refresh() },
                        onUserClick = onUserClick
                    )
                }

                is UiState.Error -> {
                    ErrorContent(
                        error = state.error,
                        onRetry = { viewModel.loadUsers() }
                    )
                }
            }
        }
    }
}

@Composable
private fun UsersListContent(
    users: List<User>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onUserClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = users,
            key = { it.id }
        ) { user ->
            UserCard(
                user = user,
                onClick = { onUserClick(user.id) }
            )
        }
    }
}

@Composable
private fun UserCard(
    user: User,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Аватар
            AsyncImage(
                model = user.image,
                contentDescription = "Аватар ${user.firstName}",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            // Информация о пользователе
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
        Icon(
            imageVector = when (error) {
                is com.example.kt6_3.domain.model.AppError.NetworkError ->
                    Icons.Default.Search
                else -> Icons.Default.Close
            },
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = error.getUserMessage(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
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