package com.ekoapp.ekosdk.uikit.chat.messages.viewHolder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.databinding.ItemAudioMessageSenderBinding
import com.ekoapp.ekosdk.uikit.chat.databinding.MsgDeletePopupBinding
import com.ekoapp.ekosdk.uikit.chat.messages.popUp.EkoPopUp
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoAudioMsgViewModel
import com.ekoapp.ekosdk.uikit.model.EventIdentifier

class EkoAudioMsgSenderViewHolder(
    itemView: View,
    private val itemViewModel: EkoAudioMsgViewModel,
    context: Context,
    audioPlayListener: IAudioPlayCallback
): AudioMsgBaseViewHolder(itemView, itemViewModel, context, audioPlayListener) {

    private val binding: ItemAudioMessageSenderBinding? = DataBindingUtil.bind(itemView)
    private var popUp: EkoPopUp? = null

    init {
        binding?.vmAudioMsg = itemViewModel
        addViewModelListeners()
    }

    override fun getAudioViewHolder(): AudioMsgBaseViewHolder = this

    private fun addViewModelListeners() {
        itemViewModel.onEventReceived += { event ->
            when (event.type) {
                EventIdentifier.DISMISS_POPUP -> popUp?.dismiss()
                else -> {
                }
            }
        }
    }

    override fun setMessageData(item: EkoMessage) {
        itemViewModel.getUploadProgress(item)
    }

    override fun showPopUp() {
        if (!itemViewModel.uploading.get()) {
            popUp = EkoPopUp()
            val anchor: View = itemView.findViewById(R.id.layoutAudio)
            val inflater: LayoutInflater = LayoutInflater.from(anchor.context)
            val binding: MsgDeletePopupBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.msg_delete_popup, null, true
            )
            binding.viewModel = itemViewModel
            popUp?.showPopUp(binding.root, anchor, itemViewModel, EkoPopUp.PopUpGravity.END)
        }
    }
}