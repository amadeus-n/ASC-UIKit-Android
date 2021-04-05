package com.ekoapp.ekosdk.uikit.community.setting.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.community.databinding.AmityItemSeparateContentBinding
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem

class EkoSeparateContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        EkoBaseRecyclerViewAdapter.IBinder<SettingsItem> {
    private val binding = AmityItemSeparateContentBinding.bind(itemView)

    override fun bind(data: SettingsItem?, position: Int) = Unit

}