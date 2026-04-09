package com.example.kt6_1.presentation.ui.detail

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.kt6_1.R
import com.example.kt6_1.presentation.ui.common.ErrorView
import com.example.kt6_1.presentation.ui.common.LoadingIndicator
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photoId: String,
    onNavigateBack: () -> Unit,
    viewModel: PhotoDetailViewModel = koinViewModel(
        parameters = { parametersOf(photoId) }
    )
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/jpeg")
    ) { uri ->
        uri?.let {
            viewModel.setDownloadUri(it)
            viewModel.downloadPhoto(context)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PhotoDetailEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is PhotoDetailEffect.ShowDownloadSuccess -> {
                    snackbarHostState.showSnackbar("Фото сохранено: ${effect.fileName}")
                }
                PhotoDetailEffect.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали фото") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    val photo = state.photo
                    if (photo != null && !state.isDownloading) {
                        IconButton(
                            onClick = {
                                val fileName = photo.getFileName()
                                saveFileLauncher.launch(fileName)
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_download),
                                contentDescription = "Скачать"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val isLoading = state.isLoading
            val errorMessage = state.error
            val photo = state.photo

            when {
                isLoading -> {
                    LoadingIndicator()
                }

                errorMessage != null -> {
                    ErrorView(
                        message = errorMessage,
                        onRetry = { viewModel.onEvent(PhotoDetailEvent.Retry) }
                    )
                }

                photo != null -> {
                    PhotoDetailContent(
                        photo = photo,
                        isDownloading = state.isDownloading,
                        onDownloadClick = {
                            val fileName = photo.getFileName()
                            saveFileLauncher.launch(fileName)
                        }
                    )
                }
            }

            if (state.isDownloading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = false) { },
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.padding(32.dp),
                        shape = MaterialTheme.shapes.medium,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Скачивание...")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoDetailContent(
    photo: com.example.kt6_1.domain.model.Photo,
    isDownloading: Boolean,
    onDownloadClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photo.downloadUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Фото ${photo.author}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(photo.width.toFloat() / photo.height),
                contentScale = ContentScale.Fit
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Информация о фото",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                InfoRow(label = "Автор", value = photo.author)
                InfoRow(label = "Размеры", value = photo.dimensions)
                InfoRow(label = "ID", value = photo.id)

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDownloadClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isDownloading
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_download),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Скачать фото")
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}