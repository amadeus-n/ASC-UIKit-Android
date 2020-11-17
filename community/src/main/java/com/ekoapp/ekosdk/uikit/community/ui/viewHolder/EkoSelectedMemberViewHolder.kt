package com.ekoapp.ekosdk.uikit.community.ui.viewHolder

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.common.loadImage
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.data.SelectMemberItem
import com.ekoapp.ekosdk.uikit.community.databinding.SelectedMemberItemBinding
import com.ekoapp.ekosdk.uikit.community.ui.clickListener.EkoSelectedMemberListener
import com.google.android.material.imageview.ShapeableImageView

class EkoSelectedMemberViewHolder(itemView: View,
                                  private val mClickListener: EkoSelectedMemberListener):
    RecyclerView.ViewHolder(itemView),
    EkoBaseRecyclerViewAdapter.IBinder<SelectMemberItem> {

    private val binding: SelectedMemberItemBinding? = DataBindingUtil.bind(itemView)

    override fun bind(data: SelectMemberItem?, position: Int) {

        if (data != null) {
            binding?.ivAvatar?.loadImage(data.avatarUrl)
            binding?.tvName?.text = data.name

            binding?.ivCross?.setOnClickListener {
                mClickListener.onMemberRemoved(data)
            }
        }

    }
}