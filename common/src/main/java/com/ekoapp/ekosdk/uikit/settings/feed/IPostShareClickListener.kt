package com.ekoapp.ekosdk.uikit.settings.feed

import android.content.Context
import com.ekoapp.ekosdk.feed.EkoPost

interface IPostShareClickListener {
    fun shareToMyTimeline(context: Context, post: EkoPost) {
        /*TODO Navigate to default ShareToMyTimeline page
           ex.EkoCommunityNavigation.navigateToMyTimeline(context)*/
    }

    fun shareToGroup(context: Context, post: EkoPost) {
        //TODO Navigate to default ShareTo page
    }

    fun shareToExternal(context: Context, post: EkoPost) {
        //TODO Not implement on this
    }
}