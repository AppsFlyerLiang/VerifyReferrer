package com.fascode.verifyreferrer

import android.app.Application
import android.content.Intent
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib

class MyApp: Application() {
    companion object {
        const val TAG = "AppsFlyer.MyApp"
        const val ConversionDataLoaded = BuildConfig.APPLICATION_ID + ".ConversionDataLoaded"
        const val DeepLinkingDataLoaded = BuildConfig.APPLICATION_ID + ".DeepLinkingDataLoaded"
        const val InstallReferrerLoaded = BuildConfig.APPLICATION_ID + ".InstallReferrerLoaded"
        var cv: MutableMap<String, Any>? = null
        var oaoa: MutableMap<String, String>? = null
    }
    override fun onCreate() {
        super.onCreate()
        InstallReferrerHelper.start(this)
        AppsFlyerLib.getInstance().setDebugLog(true)
        AppsFlyerLib.getInstance().init("SC6zv6Zb6N52vePBePs5Xo", listener, this)
        AppsFlyerLib.getInstance().startTracking(this)
    }


    private val listener = object: AppsFlyerConversionListener {
        override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
            Log.e(TAG, "[onAppOpenAttribution]")
            oaoa = data
            sendBroadcast(Intent(DeepLinkingDataLoaded))
        }

        override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
            Log.e(TAG, "[onConversionDataSuccess]")
            cv = data
            sendBroadcast(Intent(ConversionDataLoaded))
        }

        override fun onConversionDataFail(error: String?) {
            Log.e(TAG, "[onConversionDataFail] $error")
        }

        override fun onAttributionFailure(error: String?) {
            Log.e(TAG, "[onAttributionFailure] $error")
        }
    }
}