package com.ekoapp.ekosdk.uikit.community.setting.viewholder

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.community.databinding.AmityItemSettingsTextContentBinding
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem
import com.ekoapp.ekosdk.uikit.components.setBold
import com.ekoapp.ekosdk.uikit.components.setVisibility

class EkoSettingsTextContentViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView),
        EkoBaseRecyclerViewAdapter.IBinder<SettingsItem> {
    private val binding = AmityItemSettingsTextContentBinding.bind(itemView)

    override fun bind(data: SettingsItem?, position: Int) {
        binding.apply {
            when (data) {
                is SettingsItem.TextContent -> {
                    tvTitle.text = context.getString(data.title)
                    tvTitle.setTextColor(ContextCompat.getColor(context, data.titleTextColor))
                    setBold(tvTitle, data.isTitleBold)

                    tvDescription.text = data.description?.let(context::getString)
                    setVisibility(tvDescription, data.description != null)

                    rootView.setOnClickListener { data.callback() }
                }
                else -> {
                }
            }
        }
    }

}