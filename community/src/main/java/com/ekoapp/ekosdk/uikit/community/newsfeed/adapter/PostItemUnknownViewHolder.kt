package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.common.views.ColorPaletteUtil
import com.ekoapp.ekosdk.uikit.common.views.ColorShade
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType

class PostItemUnknownViewHolder(itemView: View): EkoBasePostViewHolder(itemView, EkoTimelineType.GLOBAL) {

    private val tvSomethingWentWrong: TextView = itemView.findViewById(R.id.tvSomethingWentWrong)
    private val tvUnRecognizedPost: TextView = itemView.findViewById(R.id.tvUnrecognizedPost)

    override fun bind(data: EkoPost?, position: Int) {
        super.bind(data, position)
        tvSomethingWentWrong.setTextColor(ColorPaletteUtil.getColor(ContextCompat.getColor(
            tvSomethingWentWrong.context, R.color.upstraColorBase
        ), ColorShade.SHADE3))

        tvUnRecognizedPost.setTextColor(ColorPaletteUtil.getColor(ContextCompat.getColor(
            tvUnRecognizedPost.context, R.color.upstraColorBase
        ), ColorShade.SHADE3))
    }
}