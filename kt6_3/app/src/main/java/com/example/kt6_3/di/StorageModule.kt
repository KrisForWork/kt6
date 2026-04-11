package com.example.kt6_3.di

import android.content.Context
import com.example.kt6_3.data.local.TokenStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {

    single { TokenStorage(androidContext()) }
}