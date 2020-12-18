package com.ekoapp.ekosdk.uikit.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EkoMenuItem(
    val id: Int,
    val title: String
) : Parcelable