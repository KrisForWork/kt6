package com.example.kt6_1.presentation.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kt6_1.domain.usecase.GetPhotosUseCase
import com.example.kt6_1.presentation.ui.common.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PhotoListViewModel(
    private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PhotoListUiState.initial())
    val state: StateFlow<PhotoListUiState> = _state.asStateFlow()

    private val _effect = Channel<PhotoListEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadPhotos()
    }

    fun onEvent(event: PhotoListEvent) {
        when (event) {
            is PhotoListEvent.LoadPhotos -> loadPhotos()
            is PhotoListEvent.Refresh -> refresh()
            is PhotoListEvent.LoadMore -> loadMore()
            is PhotoListEvent.Retry -> retry(event.message)
        }
    }

    fun onPhotoClick(photoId: String) {
        viewModelScope.launch {
            _effect.send(PhotoListEffect.NavigateToDetail(photoId))
        }
    }

    private var retryCount = 0
    private val MAX_RETRY_COUNT = 3

    private fun loadPhotos() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    photoState = UiState.Loading,
                    currentPage = 1
                )
            }

            getPhotosUseCase(page = 1).fold(
                onSuccess = { photos ->
                    retryCount = 0
                    _state.update {
                        it.copy(
                            photoState = UiState.Success(photos),
                            hasMorePages = photos.size >= 30
                        )
                    }
                },
                onFailure = { error ->
                    retryCount++
                    val errorMessage = when {
                        error is java.net.UnknownHostException -> "Отсутствует подключение к интернету"
                        error is java.net.SocketTimeoutException -> "Превышено время ожидания"
                        retryCount >= MAX_RETRY_COUNT -> "Не удалось загрузить данные после $MAX_RETRY_COUNT попыток"
                        else -> error.message ?: "Неизвестная ошибка"
                    }
                    _state.update {
                        it.copy(
                            photoState = UiState.Error(errorMessage)
                        )
                    }
                    if (retryCount < MAX_RETRY_COUNT) {
                        _effect.send(PhotoListEffect.ShowError(errorMessage))
                    }
                }
            )
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }

            getPhotosUseCase(page = 1).fold(
                onSuccess = { photos ->
                    _state.update {
                        it.copy(
                            photoState = UiState.Success(photos),
                            isRefreshing = false,
                            currentPage = 1,
                            hasMorePages = photos.size >= 30
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            photoState = UiState.Error(error.message ?: "Ошибка обновления")
                        )
                    }
                    _effect.send(PhotoListEffect.ShowError(error.message ?: "Ошибка обновления"))
                }
            )
        }
    }

    private fun loadMore() {
        val currentState = _state.value
        if (currentState.isLoading || !currentState.hasMorePages) return

        val nextPage = currentState.currentPage + 1

        viewModelScope.launch {
            getPhotosUseCase(page = nextPage).fold(
                onSuccess = { newPhotos ->
                    val allPhotos = currentState.photos + newPhotos
                    _state.update {
                        it.copy(
                            photoState = UiState.Success(allPhotos),
                            currentPage = nextPage,
                            hasMorePages = newPhotos.size >= 30
                        )
                    }
                },
                onFailure = { error ->
                    _effect.send(PhotoListEffect.ShowError(error.message ?: "Ошибка загрузки"))
                }
            )
        }
    }

    private fun retry(errorMessage: String) {
        loadPhotos()
    }
}