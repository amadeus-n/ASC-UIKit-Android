package com.ekoapp.ekosdk.uikit.community.mycommunity.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.mycommunity.listener.IMyCommunityItemClickListener

class EkoMyCommunitiesAdapter(private val listener: IMyCommunityItemClickListener) :
    EkoBaseRecyclerViewPagedAdapter<EkoCommunity>(diffCallBack) {

    override fun getLayoutId(position: Int, obj: EkoCommunity?): Int =
        R.layout.layout_community_item

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder =
        EkoMyCommunitiesViewHolder(view, listener)




    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<EkoCommunity>() {

            override fun areItemsTheSame(oldItem: EkoCommunity, newItem: EkoCommunity): Boolean =
                oldItem.getCommunityId() == newItem.getCommunityId()

            override fun areContentsTheSame(oldItem: EkoCommunity, newItem: EkoCommunity): Boolean =
                oldItem == newItem
        }
    }

}