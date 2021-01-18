package com.ekoapp.ekosdk.uikit.community.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(val id: String, val userName: String, val profileUrl: String) : Parcelable {

    fun getUserId(): String {
        return id
    }

    fun getDisplayName(): String {
        return userName
    }

}