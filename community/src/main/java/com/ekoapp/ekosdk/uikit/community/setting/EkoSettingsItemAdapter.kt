package com.ekoapp.ekosdk.uikit.community.setting

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.setting.viewholder.*

class EkoSettingsItemAdapter : EkoBaseRecyclerViewAdapter<SettingsItem>() {

    override fun getLayoutId(position: Int, obj: SettingsItem?): Int {
        obj?.let { item ->
            when (item) {
                is SettingsItem.Header -> {
                    return R.layout.amity_item_settings_header
                }
                is SettingsItem.TextContent -> {
                    return R.layout.amity_item_settings_text_content
                }
                is SettingsItem.NavigationContent -> {
                    return R.layout.amity_item_settings_nav_content
                }
                is SettingsItem.ToggleContent -> {
                    return R.layout.amity_item_settings_toggle_content
                }
                is SettingsItem.RadioContent -> {
                    return R.layout.amity_item_settings_radio_content
                }
                is SettingsItem.Margin -> {
                    return R.layout.amity_item_settings_margin
                }
                SettingsItem.Separator -> {
                    return R.layout.amity_item_separate_content
                }
            }
        }
        return R.layout.amity_item_separate_content
    }

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        val context = view.context
        return when (viewType) {
            R.layout.amity_item_settings_header -> EkoSettingsHeaderViewHolder(context, view)
            R.layout.amity_item_settings_text_content -> EkoSettingsTextContentViewHolder(context, view)
            R.layout.amity_item_settings_nav_content -> EkoSettingsNavContentViewHolder(context, view)
            R.layout.amity_item_settings_toggle_content -> EkoSettingsToggleContentViewHolder(context, view)
            R.layout.amity_item_separate_content -> EkoSeparateContentViewHolder(view)
            R.layout.amity_item_settings_radio_content -> EkoSettingsRadioContentViewHolder(context, view)
            R.layout.amity_item_settings_margin -> EkoSettingsMarginViewHolder(context, view)

            else -> EkoSettingsHeaderViewHolder(context, view)
        }
    }
}