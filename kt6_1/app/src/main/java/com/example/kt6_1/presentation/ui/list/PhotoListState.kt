package com.example.kt6_1.presentation.ui.list

import com.example.kt6_1.domain.model.Photo
import com.example.kt6_1.presentation.ui.common.UiState

data class PhotoListUiState(
    val photoState: UiState<List<Photo>> = UiState.Loading,
    val isRefreshing: Boolean = false,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true
) {
    val photos: List<Photo>
        get() = photoState.getDataOrNull() ?: emptyList()

    val isLoading: Boolean
        get() = photoState.isLoading

    val error: String?
        get() = photoState.getErrorOrNull()

    val isEmpty: Boolean
        get() = photos.isEmpty() && !isLoading

    companion object {
        fun initial() = PhotoListUiState()
    }
}

sealed class PhotoListEvent {
    data object LoadPhotos : PhotoListEvent()
    data object Refresh : PhotoListEvent()
    data object LoadMore : PhotoListEvent()
    data class Retry(val message: String) : PhotoListEvent()
}

sealed class PhotoListEffect {
    data class NavigateToDetail(val photoId: String) : PhotoListEffect()
    data class ShowError(val message: String) : PhotoListEffect()
}