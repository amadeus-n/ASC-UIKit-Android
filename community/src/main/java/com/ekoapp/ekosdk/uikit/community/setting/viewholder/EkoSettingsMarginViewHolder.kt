package com.ekoapp.ekosdk.uikit.community.setting.viewholder

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.community.databinding.AmityItemSettingsMarginBinding
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem

class EkoSettingsMarginViewHolder(private val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView),
    EkoBaseRecyclerViewAdapter.IBinder<SettingsItem> {
    private val binding = AmityItemSettingsMarginBinding.bind(itemView)

    override fun bind(data: SettingsItem?, position: Int) {
        binding.apply {
            when(data) {
                is SettingsItem.Margin -> {
                    val margin = context.resources.getDimensionPixelSize(data.margin)
                    vMargin.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, margin)
                }
                else -> {}
            }
        }
    }

}