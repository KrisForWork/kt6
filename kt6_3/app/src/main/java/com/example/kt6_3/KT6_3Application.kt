package com.example.kt6_3

import android.app.Application
import com.example.kt6_3.di.networkModule
import com.example.kt6_3.di.presentationModule
import com.example.kt6_3.di.repositoryModule
import com.example.kt6_3.di.storageModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KT6_3Application : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@KT6_3Application)
            modules(
                networkModule,
                storageModule,
                repositoryModule,
                presentationModule
            )
        }
    }
}