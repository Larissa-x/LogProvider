package com.netrain.sdlfy.sharedemo

import android.app.Application
import com.netrain.sdlfy.lib_log_cloud.BuildConfig
import com.netrain.sdlfy.lib_log_cloud.TimberProvider

class App : Application() {

    private var debug = BuildConfig.DEBUG
    override fun onCreate() {
        super.onCreate()
        TimberProvider.init(this, debug, true)
    }
}