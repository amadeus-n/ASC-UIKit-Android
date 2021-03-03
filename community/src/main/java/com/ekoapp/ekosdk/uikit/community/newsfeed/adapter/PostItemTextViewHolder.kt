package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType

class PostItemTextViewHolder(
    itemView: View,
    timelineType: EkoTimelineType
) : EkoBasePostViewHolder(itemView, timelineType) {

    private var showCompleteText = false

    internal fun showCompleteText(value: Boolean) {
        showCompleteText = value
    }

    override fun bind(data: EkoPost?, position: Int) {
        super.bind(data, position)
        if (data != null) {
            setPostText(data, position, showCompleteText)
        }
    }
}