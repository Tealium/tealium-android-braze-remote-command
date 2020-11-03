package com.tealium.example

import android.app.Application
import com.tealium.example.helper.TealiumHelper.initialize

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initialize(this)
    }
}