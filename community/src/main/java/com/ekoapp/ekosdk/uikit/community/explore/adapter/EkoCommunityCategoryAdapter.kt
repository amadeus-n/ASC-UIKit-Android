package com.ekoapp.ekosdk.uikit.community.explore.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.community.category.EkoCommunityCategory
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.AmityItemCommunityCategoryBinding
import com.ekoapp.ekosdk.uikit.community.explore.listener.IEkoCategoryItemClickListener

class EkoCommunityCategoryAdapter(private val listener: IEkoCategoryItemClickListener) :
    EkoBaseRecyclerViewPagedAdapter<EkoCommunityCategory>(diffCallBack) {

    override fun getLayoutId(position: Int, obj: EkoCommunityCategory?): Int =
        R.layout.amity_item_community_category

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder =
        EkoCommunityCategoryViewHolder(view, listener)


    inner class EkoCommunityCategoryViewHolder(
        itemView: View,
        private val listener: IEkoCategoryItemClickListener
    ) : RecyclerView.ViewHolder(itemView), Binder<EkoCommunityCategory> {

        private val binding: AmityItemCommunityCategoryBinding? = DataBindingUtil.bind(itemView)

        override fun bind(data: EkoCommunityCategory?, position: Int) {
            binding?.communityCategory = data
            binding?.listener = listener
            binding?.avatarUrl = data?.getAvatar()?.getUrl(EkoImage.Size.MEDIUM)
        }

    }

    override fun getItemCount(): Int {
        return if (super.getItemCount() < 8) {
            super.getItemCount()
        } else {
            8
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<EkoCommunityCategory>() {

            override fun areItemsTheSame(
                oldItem: EkoCommunityCategory,
                newItem: EkoCommunityCategory
            ): Boolean =
                oldItem.getCategoryId() == newItem.getCategoryId()

            override fun areContentsTheSame(
                oldItem: EkoCommunityCategory,
                newItem: EkoCommunityCategory
            ): Boolean =
                oldItem.getAvatar()?.getUrl() == newItem.getAvatar()?.getUrl()
                        && oldItem.getName() == newItem.getName()
        }
    }
}