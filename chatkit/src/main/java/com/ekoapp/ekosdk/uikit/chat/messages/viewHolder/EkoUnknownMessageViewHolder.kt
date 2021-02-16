package com.ekoapp.ekosdk.uikit.chat.messages.viewHolder

import android.view.View
import androidx.databinding.DataBindingUtil
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.chat.databinding.AmityItemUnknownMessageBinding
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoUnknownMsgViewModel

class EkoUnknownMessageViewHolder(
    itemView: View, private val itemViewModel: EkoUnknownMsgViewModel
) : EkoChatMessageBaseViewHolder(itemView, itemViewModel) {

    private val mBinding: AmityItemUnknownMessageBinding? = DataBindingUtil.bind(itemView)

    override fun setMessage(message: EkoMessage) {

    }
}