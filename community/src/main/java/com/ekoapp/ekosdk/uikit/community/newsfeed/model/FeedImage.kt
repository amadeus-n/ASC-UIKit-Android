package com.ekoapp.ekosdk.uikit.community.newsfeed.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FeedImage(
    var id: String?,
    var uploadId: String?,
    val url: Uri,
    var uploadState: FileUploadState = FileUploadState.PENDING,
    var currentProgress: Int = 0
) : Parcelable