package com.example.kt6_1.presentation.ui.detail

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kt6_1.domain.usecase.GetPhotoByIdUseCase
import com.example.kt6_1.presentation.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

class PhotoDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getPhotoByIdUseCase: GetPhotoByIdUseCase
) : ViewModel() {

    private val photoId: String = checkNotNull(savedStateHandle["photoId"])

    private val _state = MutableStateFlow(PhotoDetailUiState.initial())
    val state: StateFlow<PhotoDetailUiState> = _state.asStateFlow()

    private val _effect = Channel<PhotoDetailEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var downloadUri: android.net.Uri? = null
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    init {
        loadPhoto()
    }

    fun onEvent(event: PhotoDetailEvent) {
        when (event) {
            is PhotoDetailEvent.LoadPhoto -> loadPhoto()
            is PhotoDetailEvent.DownloadPhoto -> {}
            is PhotoDetailEvent.Retry -> loadPhoto()
        }
    }

    fun setDownloadUri(uri: android.net.Uri) {
        this.downloadUri = uri
    }

    fun downloadPhoto(context: Context) {
        val photo = _state.value.photo ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isDownloading = true) }

            try {
                downloadUri?.let { uri ->
                    downloadPhotoWithOkHttp(context, photo.downloadUrl, uri)
                }
                _effect.send(PhotoDetailEffect.ShowDownloadSuccess(photo.getFileName()))
            } catch (e: Exception) {
                _effect.send(PhotoDetailEffect.ShowError("Ошибка скачивания: ${e.message}"))
            } finally {
                _state.update { it.copy(isDownloading = false) }
            }
        }
    }

    private fun downloadPhotoWithOkHttp(context: Context, url: String, uri: android.net.Uri) {
        val request = Request.Builder().url(url).build()
        val response = okHttpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Ошибка загрузки: ${response.code}")
        }

        response.body?.byteStream()?.use { inputStream ->
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    private fun loadPhoto() {
        viewModelScope.launch {
            _state.update { it.copy(photoState = UiState.Loading) }

            getPhotoByIdUseCase(photoId).fold(
                onSuccess = { photo ->
                    _state.update { it.copy(photoState = UiState.Success(photo)) }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(photoState = UiState.Error(error.message ?: "Ошибка загрузки"))
                    }
                    _effect.send(PhotoDetailEffect.ShowError(error.message ?: "Ошибка загрузки"))
                }
            )
        }
    }
}