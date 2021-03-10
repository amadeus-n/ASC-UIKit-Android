package com.ekoapp.ekosdk.uikit.community.mycommunity.adapter

import android.view.View
import android.widget.LinearLayout
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.common.setBackgroundColor
import com.ekoapp.ekosdk.uikit.common.views.ColorShade
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.mycommunity.listener.IMyCommunityItemClickListener
import com.google.android.material.imageview.ShapeableImageView

class ViewAllCommunityPreviewItemViewHolder(itemView: View, val listener: IMyCommunityItemClickListener) : BaseMyCommunityPreviewItemViewHolder(itemView) {

    private val viewMoreLayout: LinearLayout = itemView.findViewById(R.id.layout_view_more)
    private val avatar: ShapeableImageView = itemView.findViewById(R.id.ivAvatar)

    override fun bind(data: EkoCommunity?, position: Int) {
        avatar.setBackgroundColor(null, ColorShade.SHADE4)
        avatar.setImageResource(R.drawable.amity_ic_arrow_forward)

        viewMoreLayout.setOnClickListener {
            listener.onCommunitySelected(null)
        }
    }

}