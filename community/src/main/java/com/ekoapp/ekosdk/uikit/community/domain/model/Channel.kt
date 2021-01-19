package com.ekoapp.ekosdk.uikit.community.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Channel(
        val channelName: String,
        val profileImageUrl: String,
        val backdropImageUrl: String,
        val numberOfFollowers: Long,
        val followersName: List<String>,
        val description: String,
        var verified: Boolean = false,
        var private: Boolean = false,
        var moderators: List<String> = listOf()
) : Parcelable