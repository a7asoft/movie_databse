package com.ionix.moviedatabase

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MoviesApp : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }
}