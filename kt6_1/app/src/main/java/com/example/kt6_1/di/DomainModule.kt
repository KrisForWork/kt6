package com.example.kt6_1.di

import com.example.kt6_1.domain.usecase.GetPhotoByIdUseCase
import com.example.kt6_1.domain.usecase.GetPhotosUseCase
import com.example.kt6_1.domain.usecase.ValidatePhotoDataUseCase
import org.koin.dsl.module

val domainModule = module {

    factory { GetPhotosUseCase(repository = get()) }

    factory { GetPhotoByIdUseCase(repository = get()) }

    factory { ValidatePhotoDataUseCase() }

}