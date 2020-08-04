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
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
    private val intentFilter = IntentFilter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updateInstallReferrer()
        intentFilter.addAction(MyApp.InstallReferrerLoaded)
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                MyApp.InstallReferrerLoaded -> updateInstallReferrer()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun updateInstallReferrer() {
        val referrer = InstallReferrerHelper.readReferrer(this)
        if(referrer?.isNotEmpty() == true){
            tvInstallReferrer.setTextColor(resources.getColor(R.color.colorPrimary))
            val spannableStringBuilder = SpannableStringBuilder()
            referrer["click_time"]?.toLong()?.let {
                val keyText = "click_time: "
                val valueText = if(it>0) dateFormatter.format(Date(it*1000L)) else "null"
                spannableStringBuilder.append(keyText + valueText + "\n")
                val startIndex = keyText.length
                spannableStringBuilder.setSpan(ForegroundColorSpan(Color.BLUE), startIndex, startIndex + valueText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD_ITALIC), startIndex, startIndex + valueText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            referrer["install_time"]?.toLong()?.let {
                val keyText2 = "install_begin_time: "
                val valueText2 = if(it>0) dateFormatter.format(Date(it*1000L)) else "null"
                spannableStringBuilder.append(keyText2 + valueText2 + "\n")
                val startIndex = spannableStringBuilder.indexOf(keyText2) + keyText2.length
                spannableStringBuilder.setSpan(ForegroundColorSpan(Color.BLUE), startIndex, startIndex + valueText2.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD_ITALIC), startIndex, startIndex + valueText2.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            val keyText3 = "install_referrer: "
            val valueText3 = referrer["install_referrer"] ?: "null"
            spannableStringBuilder.append(keyText3 + valueText3)
            val startIndex = spannableStringBuilder.indexOf(keyText3) + keyText3.length
            spannableStringBuilder.setSpan(ForegroundColorSpan(Color.BLUE), startIndex, startIndex + valueText3.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD_ITALIC), startIndex, startIndex + valueText3.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            tvInstallReferrer.text = spannableStringBuilder
        } else {
            tvInstallReferrer.setTextColor(resources.getColor(R.color.colorAccent))
            tvInstallReferrer.text = getString(R.string.google_referrer_not_set)
        }

    }

}