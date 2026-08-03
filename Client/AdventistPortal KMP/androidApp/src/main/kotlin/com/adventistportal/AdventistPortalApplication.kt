package com.adventistportal

import android.app.Application
import com.adventistportal.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class AdventistPortalApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@AdventistPortalApplication)
            androidLogger()
        }
    }
}