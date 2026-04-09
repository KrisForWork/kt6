package com.example.kt6_1

import android.app.Application
import com.example.kt6_1.di.allModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class PhotoCatalogApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@PhotoCatalogApplication)
            modules(allModules)
        }
    }

}