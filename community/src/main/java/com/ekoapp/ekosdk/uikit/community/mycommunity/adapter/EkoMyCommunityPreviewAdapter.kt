package com.ekoapp.ekosdk.uikit.community.mycommunity.adapter

import android.view.View
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.mycommunity.listener.IMyCommunityItemClickListener


class EkoMyCommunityPreviewAdapter(private val listener: IMyCommunityItemClickListener) :
    EkoBaseRecyclerViewPagedAdapter<EkoCommunity>(MyCommunityDiffImpl.diffCallBack) {

    private val VIEW_ALL_ITEM_POSITION = 8

    override fun getLayoutId(position: Int, obj: EkoCommunity?): Int {
        return if (position == VIEW_ALL_ITEM_POSITION) {
            R.layout.amity_item_my_community_more
        } else {
            R.layout.amity_item_my_community
        }
    }

    override fun getViewHolder(view: View, viewType: Int): BaseMyCommunityPreviewItemViewHolder {
        return when (viewType) {
            R.layout.amity_item_my_community_more -> {
                ViewAllCommunityPreviewItemViewHolder(view, listener)
            }
            else -> {
                MyCommunityPreviewItemViewHolder(view, listener)
            }
        }
    }

    override fun getItemCount(): Int {
        return Math.min(super.getItemCount(), VIEW_ALL_ITEM_POSITION + 1)
    }

}