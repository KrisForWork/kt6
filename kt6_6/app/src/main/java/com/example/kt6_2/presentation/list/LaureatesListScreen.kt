package com.example.kt6_2.presentation.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.kt6_2.domain.model.Laureate
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kt6_2.domain.model.NobelPrize
import com.example.kt6_2.presentation.list.components.FilterSection
import com.example.kt6_2.presentation.list.components.LaureateCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaureatesListScreen(
    viewModel: LaureatesListViewModel,
    onLaureateClick: (prize: NobelPrize, laureate: Laureate) -> Unit,
    onFavoritesClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nobel Prize Laureates") },
                actions = {
                    // Кнопка избранного
                    IconButton(onClick = onFavoritesClick) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorites"
                        )
                    }
                    // Кнопка выхода
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Logout"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Filter section
            FilterSection(
                viewModel = viewModel,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Content based on state
            when (val currentState = state) {
                is LaureatesListState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LaureatesListState.Success -> {
                    if (currentState.prizes.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No laureates found",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(
                                items = currentState.prizes.flatMap { prize ->
                                    prize.laureates.map { laureate ->
                                        prize to laureate
                                    }
                                },
                                key = { (prize, laureate) -> "${prize.awardYear}_${prize.category}_${laureate.id}" }
                            ) { (prize, laureate) ->
                                LaureateCard(
                                    prize = prize,
                                    laureate = laureate,
                                    onClick = {
                                        onLaureateClick(prize, laureate)
                                    }
                                )
                            }
                        }
                    }
                }

                is LaureatesListState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = currentState.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { viewModel.retry() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}