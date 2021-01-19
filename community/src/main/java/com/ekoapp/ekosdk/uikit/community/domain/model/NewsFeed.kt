package com.ekoapp.ekosdk.uikit.community.domain.model

import android.os.Parcelable
import com.ekoapp.ekosdk.uikit.community.newsfeed.model.FeedImage
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewsFeed(
        var id: String?,
        var text: String,
        val displayName: String,
        val avatarUrl: String? = null,
        val postTime: Long,
        val numLikes: Int,
        val numComments: Int,
        val liked: Boolean = false,
        var images: List<FeedImage>? = null,
        var attachments: List<FileAttachment>? = null,
        var edited: Boolean = false,
        var postedByModerator: Boolean = false,
        var user: User? = null,
        var channel: Channel? = null
) : Parcelable