package com.example.kt6_1.presentation.ui.detail

import com.example.kt6_1.domain.model.Photo
import com.example.kt6_1.presentation.ui.common.UiState

data class PhotoDetailUiState(
    val photoState: UiState<Photo> = UiState.Loading,
    val isDownloading: Boolean = false,
    val downloadProgress: Float? = null
) {
    val photo: Photo?
        get() = photoState.getDataOrNull()

    val isLoading: Boolean
        get() = photoState.isLoading

    val error: String?
        get() = photoState.getErrorOrNull()

    companion object {
        fun initial() = PhotoDetailUiState()
    }
}

sealed class PhotoDetailEvent {
    data class LoadPhoto(val photoId: String) : PhotoDetailEvent()
    data object DownloadPhoto : PhotoDetailEvent()
    data object Retry : PhotoDetailEvent()
}

sealed class PhotoDetailEffect {
    data class ShowError(val message: String) : PhotoDetailEffect()
    data class ShowDownloadSuccess(val fileName: String) : PhotoDetailEffect()
    data object NavigateBack : PhotoDetailEffect()
}