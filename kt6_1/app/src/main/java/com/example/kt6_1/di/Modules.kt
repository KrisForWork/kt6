package com.example.kt6_1.di

import org.koin.core.module.Module

val allModules: List<Module> = listOf(
    networkModule,
    repositoryModule,
    domainModule,
    viewModelModule,
    appModule
)