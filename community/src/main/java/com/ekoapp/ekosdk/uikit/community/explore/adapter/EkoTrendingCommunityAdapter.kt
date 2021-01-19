package com.ekoapp.ekosdk.uikit.community.explore.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.common.formatCount
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutTrendingCommunityListItemBinding
import com.ekoapp.ekosdk.uikit.community.mycommunity.listener.IMyCommunityItemClickListener

class EkoTrendingCommunityAdapter(private val listener: IMyCommunityItemClickListener) :
        EkoBaseRecyclerViewPagedAdapter<EkoCommunity>(diffCallBack) {

    override fun getLayoutId(position: Int, obj: EkoCommunity?): Int =
            R.layout.layout_trending_community_list_item

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder =
            EkoTrendingCommunityViewHolder(view, listener)

    override fun getItemCount(): Int {
        return if (super.getItemCount() < 5) {
            super.getItemCount()
        } else {
            5
        }
    }

    class EkoTrendingCommunityViewHolder(
            itemView: View,
            private val listener: IMyCommunityItemClickListener
    ) :
            RecyclerView.ViewHolder(itemView), Binder<EkoCommunity> {

        private val binding: LayoutTrendingCommunityListItemBinding? =
                DataBindingUtil.bind(itemView)

        override fun bind(data: EkoCommunity?, position: Int) {
            binding?.avatarUrl = data?.getAvatar()?.getUrl(EkoImage.Size.MEDIUM)
            binding?.ekoCommunity = data
            binding?.listener = listener
            binding?.tvCount?.text = "${position + 1}"
            binding?.tvMembersCount?.text = itemView.context.getString(
                    R.string.members_count,
                    "${data?.getMemberCount()?.toDouble()?.formatCount()}"
            )
            binding?.tvCategory?.text =
                    data?.getCategories()?.joinToString(separator = " ") { it.getName() }
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<EkoCommunity>() {

            override fun areItemsTheSame(oldItem: EkoCommunity, newItem: EkoCommunity): Boolean =
                    oldItem.getCommunityId() == newItem.getCommunityId()

            override fun areContentsTheSame(oldItem: EkoCommunity, newItem: EkoCommunity): Boolean =
                    oldItem.getAvatar()?.getUrl() == newItem.getAvatar()?.getUrl()
                            && oldItem.getDisplayName() == newItem.getDisplayName()
                            && oldItem.isOfficial() == newItem.isOfficial()
        }
    }
}