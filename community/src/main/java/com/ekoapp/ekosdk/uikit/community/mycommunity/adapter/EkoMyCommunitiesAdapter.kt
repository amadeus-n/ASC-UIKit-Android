package com.ekoapp.ekosdk.uikit.community.mycommunity.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.mycommunity.listener.IMyCommunityItemClickListener

class EkoMyCommunitiesAdapter(private val listener: IMyCommunityItemClickListener) :
    EkoBaseRecyclerViewPagedAdapter<EkoCommunity>(MyCommunityDiffImpl.diffCallBack) {

    override fun getLayoutId(position: Int, obj: EkoCommunity?): Int =
        R.layout.amity_item_community

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder =
        EkoMyCommunitiesViewHolder(view, listener)


}