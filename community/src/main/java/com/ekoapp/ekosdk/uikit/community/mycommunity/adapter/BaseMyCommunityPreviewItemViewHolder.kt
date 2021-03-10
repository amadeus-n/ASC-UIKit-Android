package com.ekoapp.ekosdk.uikit.community.mycommunity.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter


abstract class BaseMyCommunityPreviewItemViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView), EkoBaseRecyclerViewPagedAdapter.Binder<EkoCommunity>
