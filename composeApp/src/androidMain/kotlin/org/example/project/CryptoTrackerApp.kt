package org.example.project

import android.app.Application
import org.example.project.di.initKoin
import org.koin.android.ext.koin.androidContext

class CryptoTrackerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@CryptoTrackerApp)
        }
    }
}