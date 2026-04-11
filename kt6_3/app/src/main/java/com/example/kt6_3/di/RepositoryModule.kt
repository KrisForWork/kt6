package com.example.kt6_3.di

import com.example.kt6_3.data.repository.AuthRepositoryImpl
import com.example.kt6_3.data.repository.UserRepositoryImpl
import com.example.kt6_3.domain.repository.AuthRepository
import com.example.kt6_3.domain.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    single<UserRepository> { UserRepositoryImpl(get(), get()) }
}