package com.ekoapp.ekosdk.uikit.utils

import android.net.Uri

interface FileDownloadStatus {
    fun onDownloadComplete(fileUri: Uri)
    fun onError(error: String?)
    fun onProgressUpdate(progress: Int)
}