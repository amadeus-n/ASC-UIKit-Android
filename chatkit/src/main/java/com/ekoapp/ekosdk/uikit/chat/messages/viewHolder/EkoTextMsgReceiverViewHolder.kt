package com.ekoapp.ekosdk.uikit.chat.messages.viewHolder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.databinding.AmityItemTextMessageReceiverBinding
import com.ekoapp.ekosdk.uikit.chat.databinding.AmityPopupMsgReportBinding
import com.ekoapp.ekosdk.uikit.chat.messages.popUp.EkoPopUp
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoTextMessageViewModel
import com.ekoapp.ekosdk.uikit.components.ILongPressListener
import com.ekoapp.ekosdk.uikit.model.EventIdentifier

class EkoTextMsgReceiverViewHolder(
    itemView: View,
    private val itemViewModel: EkoTextMessageViewModel,
    context: Context
) : EkoSelectableMessageViewHolder(itemView, itemViewModel, context), ILongPressListener {

    private val binding: AmityItemTextMessageReceiverBinding? = DataBindingUtil.bind(itemView)
    private var popUp: EkoPopUp? = null

    init {
        binding?.vmTextMessage = itemViewModel
        binding?.lonPressListener = this
        addViewModelListener()
    }

    private fun addViewModelListener() {
        itemViewModel.onEventReceived += { event ->
            when (event.type) {
                EventIdentifier.DISMISS_POPUP -> popUp?.dismiss()
                else -> {
                }
            }
        }
    }

    override fun setMessageData(item: EkoMessage) {
        val data = item.getData() as EkoMessage.Data.TEXT
        itemViewModel.text.set(data.getText())
        //binding?.tvMessageIncoming?.setReadMoreColor(R.color.ekoColorHighlight)
    }

    override fun showPopUp() {
        popUp = EkoPopUp()
        val anchor: View = itemView.findViewById(R.id.tvMessageIncoming)
        val inflater: LayoutInflater = LayoutInflater.from(anchor.context)
        val binding: AmityPopupMsgReportBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.amity_popup_msg_report, null, true
        )
        binding.viewModel = itemViewModel
        popUp?.showPopUp(binding.root, anchor, itemViewModel, EkoPopUp.PopUpGravity.START)
    }

    override fun onLongPress() {
        itemViewModel.onLongPress()
    }
}