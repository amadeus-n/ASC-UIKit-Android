package com.ekoapp.ekosdk.uikit.community.setting.viewholder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.community.databinding.AmityItemSettingsNavContentBinding
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem
import com.ekoapp.ekosdk.uikit.components.setBold
import com.ekoapp.ekosdk.uikit.components.setImageResource
import com.ekoapp.ekosdk.uikit.components.setVisibility

class EkoSettingsNavContentViewHolder(val context: Context, itemView: View) :
        RecyclerView.ViewHolder(itemView),
        EkoBaseRecyclerViewAdapter.IBinder<SettingsItem> {
    private val binding = AmityItemSettingsNavContentBinding.bind(itemView)

    override fun bind(data: SettingsItem?, position: Int) {
        binding.apply {
            when (data) {
                is SettingsItem.NavigationContent -> {
                    data.icon?.let { setImageResource(mainSettingsContent.ivIcon, it) }
                    setVisibility(mainSettingsContent.ivIcon, data.icon != null)

                    mainSettingsContent.tvTitle.text = context.getString(data.title)
                    setBold(mainSettingsContent.tvTitle, data.isTitleBold)

                    mainSettingsContent.tvDescription.text = data.description?.let(context::getString)
                    setVisibility(mainSettingsContent.tvDescription, data.description != null)

                    tvValue.text = data.value?.let(context::getString)
                    setVisibility(tvValue, data.value != null)

                    data.iconNavigation?.let { setImageResource(ivNavigation, it) }
                    setVisibility(ivNavigation, data.iconNavigation != null)

                    rootView.setOnClickListener { data.callback() }
                }
                else -> {
                }
            }
        }
    }
}