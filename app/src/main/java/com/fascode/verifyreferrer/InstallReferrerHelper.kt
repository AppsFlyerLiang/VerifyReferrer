package com.fascode.verifyreferrer

import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails

object InstallReferrerHelper {
    const val TAG = "AppsFlyer.MyApp"
    private lateinit var referrerClient: InstallReferrerClient
    fun start(context: Context) {
        referrerClient = InstallReferrerClient.newBuilder(context).build()
        Log.i(TAG, "[InstallReferrerHelper][start]")
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                Log.i(TAG, "[InstallReferrerHelper][onInstallReferrerSetupFinished] called")
                var response: ReferrerDetails? = null
                var referrer: MutableMap<String, Any> = mutableMapOf("response_code" to "OK")
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        Log.i(TAG, "[InstallReferrerHelper][onInstallReferrerSetupFinished] OK")
                        try{
                            if(referrerClient.isReady) {
                                response = referrerClient.installReferrer
                                referrerClient.endConnection()
                            } else {
                                Log.e(TAG, "[InstallReferrerHelper][onInstallReferrerSetupFinished] OK but referrerClient.isReady=false")
                            }
                        }catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        Log.i(TAG, "[InstallReferrerHelper][onInstallReferrerSetupFinished] FEATURE_NOT_SUPPORTED")
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        Log.i(TAG, "[InstallReferrerHelper][onInstallReferrerSetupFinished] SERVICE_UNAVAILABLE")
                    }
                    else -> {
                        Log.i(TAG, "[InstallReferrerHelper][onInstallReferrerSetupFinished] else")
                    }
                }
                handleReferrer(response, referrer)
            }
            private fun handleReferrer(response: ReferrerDetails?, referrer: MutableMap<String, Any>) {
                try {
                    if (response != null) {
                        if (response.installReferrer != null) {
                            referrer["install_referrer"] = response.installReferrer
                        }
                        referrer["click_time"] = response.referrerClickTimestampSeconds
                        referrer["install_time"] = response.installBeginTimestampSeconds
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    referrer["install_referrer"] = "-1"
                    referrer["click_time"] = "-1"
                    referrer["install_time"] = "-1"
                }
                Log.i(TAG, "[handleReferrer] Install referrer:")
                saveReferrer(context, referrer)
                referrer.forEach { (t, u) -> Log.i(TAG, "$t: $u") }
            }

            override fun onInstallReferrerServiceDisconnected() {
                Log.i(TAG, "[onInstallReferrerServiceDisconnected]")
            }
        })
    }

    private fun saveReferrer(context: Context, referrer: Map<String, Any>) {
        val prefs = context.getSharedPreferences("install_referrer", Context.MODE_PRIVATE)
        referrer.map { "${it.key}:${it.value}" }.toSet().let {
            prefs.edit().putStringSet("referrer", it).apply()
        }
        context.sendBroadcast(Intent(MyApp.InstallReferrerLoaded))
    }

    fun readReferrer(context: Context) : Map<String, String>? {
        var result:MutableMap<String, String>? = null
        val prefs = context.getSharedPreferences("install_referrer", Context.MODE_PRIVATE)
        val stringSet = prefs.getStringSet("referrer", null)
        stringSet?.let {
            result = mutableMapOf()
            it.forEach { item ->
                item.split(":").takeIf { i -> i.size>1 }?.let {list ->
                    result?.put(list[0], list[1])
                }
            }
        }
        return result
    }
}