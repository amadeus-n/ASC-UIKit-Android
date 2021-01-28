package com.ekoapp.ekosdk.uikit.chat.messages.viewHolder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.databinding.ItemTextMessageSenderBinding
import com.ekoapp.ekosdk.uikit.chat.databinding.TextMsgSenderPopupBinding
import com.ekoapp.ekosdk.uikit.chat.editMessage.EkoEditMessageActivity
import com.ekoapp.ekosdk.uikit.chat.messages.popUp.EkoPopUp
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoTextMessageViewModel
import com.ekoapp.ekosdk.uikit.components.ILongPressListener
import com.ekoapp.ekosdk.uikit.model.EventIdentifier

class EkoTextMsgSenderViewHolder(
    itemView: View,
    private val itemViewModel: EkoTextMessageViewModel,
    context: Context
) : EkoSelectableMessageViewHolder(itemView, itemViewModel, context), ILongPressListener {

    private val binding: ItemTextMessageSenderBinding? = DataBindingUtil.bind(itemView)
    private var popUp: EkoPopUp? = null

    init {
        binding?.vmTextMessage = itemViewModel
        binding?.lonPressListener = this
        addViewModelListener()
    }

    private fun addViewModelListener() {
        itemViewModel.onEventReceived += { event ->
            when (event.type) {
                EventIdentifier.EDIT_MESSAGE -> {
                    navigateToEditMessage()
                    popUp?.dismiss()
                }
                EventIdentifier.DISMISS_POPUP -> popUp?.dismiss()
                else -> {
                }
            }
        }
    }

    override fun setMessageData(item: EkoMessage) {
        val data = item.getData() as EkoMessage.Data.TEXT
        itemViewModel.text.set(data.getText())
    }

    override fun showPopUp() {
        popUp = EkoPopUp()
        val anchor: View = itemView.findViewById(R.id.tvMessageOutgoing)
        val inflater: LayoutInflater = LayoutInflater.from(anchor.context)
        val binding: TextMsgSenderPopupBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.text_msg_sender_popup, null, true
        )
        binding.viewModel = itemViewModel

        if (!itemViewModel.isFailed.get()) {
            popUp?.showPopUp(binding.root, anchor, itemViewModel, EkoPopUp.PopUpGravity.END)
        }

    }

    private fun navigateToEditMessage() {
        val intent = EkoEditMessageActivity.newIntent(
            itemView.context,
            itemViewModel.ekoMessage?.getMessageId() ?: ""
        )
        itemView.context.startActivity(intent)
    }

    override fun onLongPress() {
        itemViewModel.onLongPress()
    }
}