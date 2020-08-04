package com.fascode.verifyreferrer

import android.app.Application

class MyApp: Application() {
    companion object {
        const val TAG = "AppsFlyer.MyApp"
        const val InstallReferrerLoaded = "com.fascode.verifyreferrer.InstallReferrerLoaded"
    }
    override fun onCreate() {
        super.onCreate()
        InstallReferrerHelper.start(this)
    }
}