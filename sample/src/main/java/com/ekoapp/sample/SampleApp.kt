package com.ekoapp.sample

import android.app.Application
import com.ekoapp.ekosdk.EkoClient

class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val apiKey = "Replace with your own key"
        EkoClient.setup(apiKey)
    }
}