package com.ekoapp.ekosdk.uikit.community.domain.model

import android.net.Uri
import android.os.Parcelable
import com.ekoapp.ekosdk.uikit.community.newsfeed.model.FileUploadState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FileAttachment(
    val id: String?,
    val uploadId: String?,
    val name: String,
    val size: Long,
    val uri: Uri,
    val readableSize: String,
    val mimeType: String,
    var uploadState: FileUploadState = FileUploadState.PENDING,
    var progress: Int = 0
) :
    Parcelable {


    override fun equals(other: Any?): Boolean {

        return (other is FileAttachment)
                && name == other.name
                && size == other.size
                && uri == other.uri
                && mimeType == other.mimeType
                && uploadId == other.uploadId
    }
}