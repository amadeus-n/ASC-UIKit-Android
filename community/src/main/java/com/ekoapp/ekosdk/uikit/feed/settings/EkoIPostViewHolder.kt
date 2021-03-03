package com.ekoapp.ekosdk.uikit.feed.settings

import android.view.View
import com.ekoapp.ekosdk.uikit.community.newsfeed.adapter.EkoBasePostViewHolder
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType

interface EkoIPostViewHolder {

    fun getDataType(): String

    fun getLayoutId(): Int

    fun createViewHolder(view: View, timelineType: EkoTimelineType): EkoBasePostViewHolder

    fun useEkoHeader(): Boolean

    fun useEkoFooter(): Boolean
}