package com.example.kt6_2.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kt6_2.data.api.service.NobelApiService
import com.example.kt6_2.data.repository.NobelRepositoryImpl
import com.example.kt6_2.domain.repository.NobelRepository
import com.example.kt6_2.domain.usecase.GetLaureatesUseCase
import com.example.kt6_2.presentation.list.LaureatesListViewModel

object AppModule {

    private val apiService: NobelApiService by lazy {
        NobelApiService(NetworkModule.httpClient)
    }

    private val nobelRepository: NobelRepository by lazy {
        NobelRepositoryImpl(apiService)
    }

    private val getLaureatesUseCase: GetLaureatesUseCase by lazy {
        GetLaureatesUseCase(nobelRepository)
    }

    fun provideLaureatesListViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LaureatesListViewModel(getLaureatesUseCase) as T
            }
        }
    }
}