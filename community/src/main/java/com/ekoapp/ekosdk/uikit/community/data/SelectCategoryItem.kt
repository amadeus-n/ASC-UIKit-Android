package com.ekoapp.ekosdk.uikit.community.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SelectCategoryItem(
        var categoryId: String = "",
        var name: String = ""
) : Parcelable