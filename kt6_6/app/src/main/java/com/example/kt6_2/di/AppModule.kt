package com.example.kt6_2.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kt6_2.data.api.service.AuthApiService
import com.example.kt6_2.data.api.service.FavoritesApiService
import com.example.kt6_2.data.api.service.FavoritesApiServiceImpl
import com.example.kt6_2.data.api.service.NobelApiService
import com.example.kt6_2.data.auth.TokenManager
import com.example.kt6_2.data.repository.AuthRepositoryImpl
import com.example.kt6_2.data.repository.FavoritesRepositoryImpl
import com.example.kt6_2.data.repository.NobelRepositoryImpl
import com.example.kt6_2.domain.repository.AuthRepository
import com.example.kt6_2.domain.repository.FavoritesRepository
import com.example.kt6_2.domain.repository.NobelRepository
import com.example.kt6_2.domain.usecase.AddFavoriteUseCase
import com.example.kt6_2.domain.usecase.GetCurrentUserUseCase
import com.example.kt6_2.domain.usecase.GetFavoritesUseCase
import com.example.kt6_2.domain.usecase.GetLaureatesUseCase
import com.example.kt6_2.domain.usecase.LoginUseCase
import com.example.kt6_2.domain.usecase.LogoutUseCase
import com.example.kt6_2.domain.usecase.RegisterUseCase
import com.example.kt6_2.domain.usecase.RemoveFavoriteUseCase
import com.example.kt6_2.presentation.auth.AuthViewModel
import com.example.kt6_2.presentation.favorites.FavoritesViewModel
import com.example.kt6_2.presentation.list.LaureatesListViewModel

object AppModule {

    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    // Token Manager
    private val tokenManager by lazy {
        TokenManager(applicationContext)
    }

    // HTTP Client
    private val httpClient by lazy {
        NetworkModule.provideHttpClient()
    }

    // API Services
    private val nobelApiService: NobelApiService by lazy {
        NetworkModule.provideNobelApiService(httpClient, tokenManager)
    }

    private val authApiService: AuthApiService by lazy {
        NetworkModule.provideAuthApiService(httpClient)
    }

    private val favoritesApiService: FavoritesApiService by lazy {
        FavoritesApiServiceImpl(httpClient, tokenManager)
    }

    // Repositories
    private val nobelRepository: NobelRepository by lazy {
        NobelRepositoryImpl(nobelApiService)
    }

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApiService, tokenManager)
    }

    private val favoritesRepository: FavoritesRepository by lazy {
        FavoritesRepositoryImpl(favoritesApiService)
    }

    // Use Cases
    private val getLaureatesUseCase by lazy {
        GetLaureatesUseCase(nobelRepository)
    }

    private val loginUseCase by lazy {
        LoginUseCase(authRepository)
    }

    private val registerUseCase by lazy {
        RegisterUseCase(authRepository)
    }

    private val logoutUseCase by lazy {
        LogoutUseCase(authRepository)
    }

    private val getCurrentUserUseCase by lazy {
        GetCurrentUserUseCase(authRepository)
    }

    private val getFavoritesUseCase by lazy {
        GetFavoritesUseCase(favoritesRepository)
    }

    private val addFavoriteUseCase by lazy {
        AddFavoriteUseCase(favoritesRepository)
    }

    private val removeFavoriteUseCase by lazy {
        RemoveFavoriteUseCase(favoritesRepository)
    }

    fun provideLaureatesListViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LaureatesListViewModel(
                    getLaureatesUseCase = getLaureatesUseCase
                ) as T
            }
        }
    }

    fun provideAuthViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(
                    loginUseCase = loginUseCase,
                    registerUseCase = registerUseCase,
                    logoutUseCase = logoutUseCase
                ) as T
            }
        }
    }

    fun provideFavoritesViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FavoritesViewModel(
                    getFavoritesUseCase = getFavoritesUseCase,
                    addFavoriteUseCase = addFavoriteUseCase,
                    removeFavoriteUseCase = removeFavoriteUseCase
                ) as T
            }
        }
    }

    fun provideGetCurrentUserUseCase(): GetCurrentUserUseCase {
        return getCurrentUserUseCase
    }
}