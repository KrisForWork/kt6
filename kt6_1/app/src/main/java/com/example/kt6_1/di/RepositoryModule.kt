package com.example.kt6_1.di

import com.example.kt6_1.data.repository.PhotoRepositoryImpl
import com.example.kt6_1.domain.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val repositoryModule = module {

    // Предоставляем IO Dispatcher
    single { Dispatchers.IO }

    // Связываем интерфейс с реализацией
    single<PhotoRepository> {
        PhotoRepositoryImpl(
            api = get(),
            ioDispatcher = get()
        )
    }

}