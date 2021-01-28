package com.ekoapp.ekosdk.uikit.community.explore.adapter

import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.common.formatCount
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutRecommCommItemBinding
import com.ekoapp.ekosdk.uikit.community.mycommunity.listener.IMyCommunityItemClickListener

class EkoRecommendedCommunitiesAdapter(private val listener: IMyCommunityItemClickListener) :
    EkoBaseRecyclerViewPagedAdapter<EkoCommunity>(diffCallBack) {

    override fun getLayoutId(position: Int, obj: EkoCommunity?): Int =
        R.layout.layout_recomm_comm_item

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder =
        EkoRecommendedCommunityViewHolder(view, listener)

    inner class EkoRecommendedCommunityViewHolder(
        itemView: View,
        private val listener: IMyCommunityItemClickListener
    ) : RecyclerView.ViewHolder(itemView), Binder<EkoCommunity> {

        private val binding: LayoutRecommCommItemBinding? = DataBindingUtil.bind(itemView)
        private val textviewCommunityName: TextView = itemView.findViewById(R.id.tvCommName)

        override fun bind(data: EkoCommunity?, position: Int) {
            if (data?.isOfficial() == true) {
                textviewCommunityName.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_uikit_verified,
                    0
                )
            } else {
                textviewCommunityName.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
            }

            binding?.ekoCommunity = data
            binding?.listener = listener
            binding?.tvMembersCount?.text = itemView.context.getString(
                R.string.members_count,
                "${data?.getMemberCount()?.toDouble()?.formatCount()}"
            )
            binding?.tvCommName?.text =
                data?.getCategories()?.joinToString(separator = " ") { it.getName() }
        }

    }

    override fun getItemCount(): Int {
        return if (super.getItemCount() < 4) {
            super.getItemCount()
        } else {
            4
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