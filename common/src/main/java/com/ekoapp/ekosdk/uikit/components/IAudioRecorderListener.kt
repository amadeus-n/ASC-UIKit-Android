package com.ekoapp.ekosdk.uikit.components

import java.io.File

interface IAudioRecorderListener {

    fun onFileRecorded(audioFile: File?)

    fun showMessage()
}