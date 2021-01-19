package com.ekoapp.ekosdk.uikit.community.mycommunity.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutCommunityItemBinding
import com.ekoapp.ekosdk.uikit.community.mycommunity.listener.IMyCommunityItemClickListener

class EkoMyCommunitiesViewHolder(
        itemView: View,
        private val listener: IMyCommunityItemClickListener
) : RecyclerView.ViewHolder(itemView), EkoBaseRecyclerViewPagedAdapter.Binder<EkoCommunity> {
    private val binding: LayoutCommunityItemBinding? = DataBindingUtil.bind(itemView)

    override fun bind(data: EkoCommunity?, position: Int) {
        binding?.ekoCommunity = data
        binding?.listener = listener
        binding?.avatarUrl = data?.getAvatar()?.getUrl(EkoImage.Size.MEDIUM)
    }
}