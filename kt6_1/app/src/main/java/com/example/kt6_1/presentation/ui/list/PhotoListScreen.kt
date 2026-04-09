package com.example.kt6_1.presentation.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kt6_1.presentation.ui.common.ErrorView
import com.example.kt6_1.presentation.ui.common.LoadingIndicator
import com.example.kt6_1.presentation.ui.common.PhotoCardShimmer
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoListScreen(
    viewModel: PhotoListViewModel = koinViewModel(),
    onPhotoClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val lazyGridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PhotoListEffect.NavigateToDetail -> {
                    onPhotoClick(effect.photoId)
                }
                is PhotoListEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    LaunchedEffect(lazyGridState) {
        snapshotFlow { lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                val totalItems = state.photos.size
                if (lastVisibleIndex != null &&
                    lastVisibleIndex >= totalItems - 6 &&
                    !state.isLoading &&
                    state.hasMorePages) {
                    viewModel.onEvent(PhotoListEvent.LoadMore)
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Фотокаталог") },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.onEvent(PhotoListEvent.Refresh) },
            modifier = Modifier.padding(paddingValues)
        ) {
            val isLoading = state.isLoading
            val photos = state.photos
            val errorMessage = state.error

            when {
                isLoading && photos.isEmpty() -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(6) {
                            PhotoCardShimmer()
                        }
                    }
                }

                errorMessage != null && photos.isEmpty() -> {
                    ErrorView(
                        message = errorMessage,
                        onRetry = { viewModel.onEvent(PhotoListEvent.Retry(errorMessage)) }
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = lazyGridState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
                    ) {
                        items(
                            items = photos,
                            key = { photo -> photo.id }
                        ) { photo ->
                            PhotoCard(
                                photo = photo,
                                onClick = { viewModel.onPhotoClick(photo.id) }
                            )
                        }

                        if (isLoading && photos.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    androidx.compose.foundation.layout.Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = androidx.compose.ui.Alignment.Center
                                    ) {
                                        LoadingIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}