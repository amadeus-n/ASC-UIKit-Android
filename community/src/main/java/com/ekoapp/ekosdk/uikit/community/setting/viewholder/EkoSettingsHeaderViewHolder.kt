package com.ekoapp.ekosdk.uikit.community.setting.viewholder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.community.databinding.AmityItemSettingsHeaderBinding
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem

class EkoSettingsHeaderViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView),
        EkoBaseRecyclerViewAdapter.IBinder<SettingsItem> {
    private val binding = AmityItemSettingsHeaderBinding.bind(itemView)

    override fun bind(data: SettingsItem?, position: Int) {
        binding.apply {
            when (data) {
                is SettingsItem.Header -> {
                    tvHeader.text = context.getString(data.title)
                }
                else -> {
                }
            }
        }
    }

}