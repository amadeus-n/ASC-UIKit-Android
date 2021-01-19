package com.ekoapp.ekosdk.uikit.community.data

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.databinding.ObservableBoolean
import com.ekoapp.ekosdk.uikit.utils.EkoConstants

data class EkoCommunityHeaderCustomization(
        var avatarUrl: String = EkoConstants.EMPTY_STRING,
        var avatarPlaceHolder: Drawable? = null,
        var avatarIsCircular: Boolean = true,
        var avatarSignature: String = EkoConstants.EMPTY_STRING,
        var chTitle: String = EkoConstants.EMPTY_STRING,
        var post: String = EkoConstants.EMPTY_STRING,
        var postBold: List<String> = arrayListOf(""),
        var postBoldRange: List<Pair<Int, Int>> = arrayListOf(),
        var followers: String = EkoConstants.EMPTY_STRING,
        var followersBold: List<String> = arrayListOf(),
        var followersBoldRange: List<Pair<Int, Int>> = arrayListOf(),
        var mutualFriends: String = EkoConstants.EMPTY_STRING,
        var mutualFriendsBold: List<String> = arrayListOf(),
        var mutualFriendsBoldRange: List<Pair<Int, Int>> = arrayListOf(),
        var boldTextColor: Int = Color.BLACK,
        var description: String = EkoConstants.EMPTY_STRING,
        var buttonFollowText: String = EkoConstants.EMPTY_STRING,
        var buttonFollowingText: String = EkoConstants.EMPTY_STRING,
        var buttonDrawable: Drawable? = null,
        var followingStatus: ObservableBoolean = ObservableBoolean(false)
)