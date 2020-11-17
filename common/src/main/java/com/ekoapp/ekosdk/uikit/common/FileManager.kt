package com.ekoapp.ekosdk.uikit.common

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.ekoapp.ekosdk.internal.api.http.EkoOkHttp
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import com.tonyodev.fetch2core.Func
import com.tonyodev.fetch2okhttp.OkHttpDownloader

class FileManager {
    companion object {
        fun saveFile(context: Context, url: String, fileName: String, mimeType: String) {
            if (url.isNotEmptyOrBlank() && fileName.isNotEmptyOrBlank()) {
                val dirPath = getFilePath(context)
                val filePath = "$dirPath/$fileName"
                val client = EkoOkHttp.newBuilder().build()

                val fetchConfiguration = FetchConfiguration.Builder(context)
                    .setDownloadConcurrentLimit(10)
                    .enableLogging(true)
                    .setHttpDownloader(OkHttpDownloader(client))
                    .setNotificationManager(object : DefaultFetchNotificationManager(context) {

                        override fun updateNotification(
                            notificationBuilder: NotificationCompat.Builder,
                            downloadNotification: DownloadNotification,
                            context: Context
                        ) {
                            super.updateNotification(
                                notificationBuilder,
                                downloadNotification,
                                context
                            )
                            if(downloadNotification.isCompleted) {
                                notificationBuilder.setContentText("Download complete.")
                            } else if (downloadNotification.isDownloading)
                                Toast.makeText(
                                    context,
                                    "Downloading file...",
                                    Toast.LENGTH_LONG
                                ).show()
                            else {
                                // do nothing
                            }
                            notificationBuilder.setContentTitle(fileName)
                        }
                    })
                    .build()

                val request = Request(url, filePath)
                request.priority = Priority.HIGH
                request.networkType = NetworkType.ALL

                val fetch = Fetch.getInstance(fetchConfiguration)
                fetch.addListener(object : FetchListener {
                    override fun onAdded(download: Download) {

                    }

                    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {

                    }

                    override fun onWaitingNetwork(download: Download) {

                    }

                    override fun onCompleted(download: Download) {
                        if (download.status == Status.COMPLETED) {
                            if (isAndroidQAndAbove()) {
                                val values = ContentValues().apply {
                                    put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
                                    put(MediaStore.Files.FileColumns.MIME_TYPE, mimeType)
                                    put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                                    put(MediaStore.Files.FileColumns.IS_PENDING, 1)
                                }
                                val resolver = context.contentResolver
                                resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                            }
                            if (!fetch.isClosed) {
                                fetch.close()
                            }
                        }
                    }

                    override fun onError(download: Download, error: Error, throwable: Throwable?) {
                        if (!fetch.isClosed) {
                            fetch.close()
                        }
                    }

                    override fun onDownloadBlockUpdated(
                        download: Download,
                        downloadBlock: DownloadBlock,
                        totalBlocks: Int
                    ) {

                    }

                    override fun onStarted(
                        download: Download,
                        downloadBlocks: List<DownloadBlock>,
                        totalBlocks: Int
                    ) {

                    }

                    override fun onProgress(
                        download: Download,
                        etaInMilliSeconds: Long,
                        downloadedBytesPerSecond: Long
                    ) {

                    }

                    override fun onPaused(download: Download) {

                    }

                    override fun onResumed(download: Download) {

                    }

                    override fun onCancelled(download: Download) {

                    }

                    override fun onRemoved(download: Download) {

                    }

                    override fun onDeleted(download: Download) {

                    }
                })

                fetch.enqueue(request, Func {
                    //Request was successfully enqueued for download.
                }, Func { error ->
                    //An error occurred enqueuing the request.
                })
            }
        }

        private fun getFilePath(context: Context): String {
            return if (isAndroidQAndAbove()) {
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()
            } else {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString()
            }
        }

        private fun isAndroidQAndAbove(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        }
    }
}