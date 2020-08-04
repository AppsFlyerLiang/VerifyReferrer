package com.fascode.verifyreferrer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val intentFilter = IntentFilter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updateConversionData()
        updateDeepLinkingData()
        intentFilter.addAction(MyApp.ConversionDataLoaded)
        intentFilter.addAction(MyApp.DeepLinkingDataLoaded)
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                MyApp.ConversionDataLoaded -> updateConversionData()
                MyApp.DeepLinkingDataLoaded -> updateDeepLinkingData()
            }
        }
    }

    private fun updateConversionData() {
        tvConversionData.text = getMapDataText(MyApp.cv).takeIf { it.isNotEmpty() } ?: "Initializing..."
    }

    private fun updateDeepLinkingData() {
        tvDeepLinkingData.text = getMapDataText(MyApp.oaoa).takeIf { it.isNotEmpty() } ?: "No Re-targeting Data!"
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun getMapDataText(map: Map<String, Any>?): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder()
        var keyText : String
        var valueText : String
        var i = 0
        map?.forEach {
            i++
            keyText = "(${String.format("%02d", i)})   ${it.key} : "
            valueText = it.value.toString()
            spannableStringBuilder.append(keyText + valueText + "\n")
            val startIndex = spannableStringBuilder.indexOf(keyText) + keyText.length
            spannableStringBuilder.setSpan(ForegroundColorSpan(Color.BLUE), startIndex, startIndex + valueText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD_ITALIC), startIndex, startIndex + valueText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        return spannableStringBuilder
    }

}