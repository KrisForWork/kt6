package com.example.kt6_1.di

import com.example.kt6_1.presentation.ui.detail.PhotoDetailViewModel
import com.example.kt6_1.presentation.ui.list.PhotoListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        PhotoListViewModel(
            getPhotosUseCase = get()
        )
    }

    viewModel { parameters ->
        PhotoDetailViewModel(
            savedStateHandle = parameters.get(),
            getPhotoByIdUseCase = get()
        )
    }

}