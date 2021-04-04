package com.ekoapp.ekosdk.uikit.community.setting.viewholder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.AmityItemSettingsRadioContentBinding
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem

class EkoSettingsRadioContentViewHolder(val context: Context, itemView: View) :
    RecyclerView.ViewHolder(itemView),
    EkoBaseRecyclerViewAdapter.IBinder<SettingsItem> {

    private val binding: AmityItemSettingsRadioContentBinding? = DataBindingUtil.bind(itemView)

    override fun bind(data: SettingsItem?, position: Int) {
        binding?.apply {
            when (data) {
                is SettingsItem.RadioContent -> {
                    var checkedIndex = -1
                    radioGroup.removeAllViews()
                    for (i in data.choices.indices) {
                        val item = data.choices[i]
                        val radioButton: RadioButton = LayoutInflater.from(context)
                            .inflate(
                                R.layout.amity_view_radio_button,
                                radioGroup,
                                false
                            ) as RadioButton
                        radioButton.text = context.getString(item.first)
                        if (item.second) {
                            checkedIndex = i
                        }
                        radioGroup.addView(radioButton)
                        radioButton.setOnClickListener {
                            data.callback(item.first)
                        }
                    }
                    if (checkedIndex != -1) {
                        (radioGroup.getChildAt(checkedIndex) as RadioButton).isChecked = true
                    }

                }
                else -> {
                }
            }
        }
    }
}