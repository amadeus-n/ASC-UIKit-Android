package com.ekoapp.ekosdk.uikit.community.explore.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.community.category.EkoCommunityCategory
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.explore.listener.IEkoCategoryItemClickListener
import com.ekoapp.ekosdk.uikit.community.views.communitycategory.EkoCommunityCategoryView

open class EkoCategoryItemViewHolder(
        itemView: View,
        val itemClickListener: IEkoCategoryItemClickListener?
) : RecyclerView.ViewHolder(itemView),
        EkoBaseRecyclerViewPagedAdapter.Binder<EkoCommunityCategory> {
    private val categoryView: EkoCommunityCategoryView = itemView.findViewById(R.id.categoryView)

    override fun bind(data: EkoCommunityCategory?, position: Int) {
        data?.let {
            categoryView.setCategory(it)
            itemView.setOnClickListener {
                itemClickListener?.onCategorySelected(data)
            }
        }

    }
}