package com.ekoapp.ekosdk.uikit.chat.messages.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoChatMessageBaseViewModel
import com.ekoapp.ekosdk.uikit.utils.EkoDateUtils

abstract class EkoChatMessageBaseViewHolder(
        itemView: View,
        val itemBaseViewModel: EkoChatMessageBaseViewModel
) : RecyclerView.ViewHolder(itemView) {

    abstract fun setMessage(message: EkoMessage)

    fun setItem(item: EkoMessage?) {
        itemBaseViewModel.ekoMessage = item
        itemBaseViewModel.msgTime.set(item?.getCreatedAt()?.toString("hh:mm a"))
        itemBaseViewModel.editedAt.set(item?.getEditedAt()?.toString("hh:mm a"))
        itemBaseViewModel.msgDate.set(
                EkoDateUtils.getRelativeDate(
                        item?.getCreatedAt()?.millis ?: 0
                )
        )
        itemBaseViewModel.isDeleted.set(item?.isDeleted() ?: false)
        itemBaseViewModel.isFailed.set(item?.getState() == EkoMessage.State.FAILED)
        if (item != null) {
            itemBaseViewModel.sender.set(getSenderName(item))
            itemBaseViewModel.isSelf.set(item.getUserId() == EkoClient.getUserId())

            val difference = item.getEditedAt().millis - item.getCreatedAt().millis
            itemBaseViewModel.isEdited.set(difference / 1000 > 1)
            setMessage(item)
        }
    }

    private fun getSenderName(item: EkoMessage): String {
        return if (item.getUserId() == EkoClient.getUserId()) {
            "ME"
        } else {
            item.getUser()?.getDisplayName() ?: itemView.context.getString(R.string.anonymous)
        }
    }
}