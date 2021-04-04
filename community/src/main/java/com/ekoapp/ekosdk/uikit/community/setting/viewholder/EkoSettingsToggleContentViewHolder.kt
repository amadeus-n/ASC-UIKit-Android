package com.ekoapp.ekosdk.uikit.community.setting.viewholder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.community.databinding.AmityItemSettingsToggleContentBinding
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem
import com.ekoapp.ekosdk.uikit.components.setBold
import com.ekoapp.ekosdk.uikit.components.setImageResource
import com.ekoapp.ekosdk.uikit.components.setVisibility
import com.ekoapp.rxlifecycle.extension.untilLifecycleEnd
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EkoSettingsToggleContentViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView),
        EkoBaseRecyclerViewAdapter.IBinder<SettingsItem> {
    private val binding = AmityItemSettingsToggleContentBinding.bind(itemView)

    override fun bind(data: SettingsItem?, position: Int) {
        binding.apply {
            when (data) {
                is SettingsItem.ToggleContent -> {
                    data.icon?.let { setImageResource(mainSettingsContent.ivIcon, it) }
                    setVisibility(mainSettingsContent.ivIcon, data.icon != null)

                    mainSettingsContent.tvTitle.text = context.getString(data.title)
                    setBold(mainSettingsContent.tvTitle, data.isTitleBold)

                    mainSettingsContent.tvDescription.text = data.description?.let(context::getString)
                    setVisibility(mainSettingsContent.tvDescription, data.description != null)

                    data.isToggled
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnNext { svButton.isChecked = it }
                            .untilLifecycleEnd(view = itemView)
                            .subscribe()

                    svButton.setOnClickListener { data.callback(svButton.isChecked) }
                }
                else -> {
                }
            }
        }
    }
}