package com.example.kt6_1.di

import android.content.Context
import com.example.kt6_1.domain.usecase.GetPhotosUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single<Context> { androidContext() }

    factory { GetPhotosUseCase(repository = get()) }
}