package com.ekoapp.ekosdk.uikit.chat.messages.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.chat.messages.viewHolder.EkoChatMessageBaseViewHolder
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoMessageListViewModel
import com.ekoapp.ekosdk.uikit.utils.EkoDateUtils

open class EkoMessageListAdapter(
    private val vmChat: EkoMessageListViewModel,
    private val iViewHolder: ICustomViewHolder?
) : PagedListAdapter<EkoMessage, EkoChatMessageBaseViewHolder>(
    MESSAGE_DIFF_CALLBACK
) {

    private val messageUtil = EkoMessageItemUtil()
    var firstCompletelyVisibleItem = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EkoChatMessageBaseViewHolder {
        return messageUtil.getViewHolder(LayoutInflater.from(parent.context), parent, viewType, iViewHolder)
    }

    override fun onBindViewHolder(holder: EkoChatMessageBaseViewHolder, position: Int) {
        holder.setItem(getItem(position))
        handleDateAndSenderVisibility(holder, position)
    }

    override fun getItemViewType(position: Int): Int {
        return messageUtil.getMessageType(getItem(position))
    }

    private fun handleDateAndSenderVisibility(holder: EkoChatMessageBaseViewHolder, position: Int) {
        val listSize = currentList?.size ?: 0
        if (listSize > 0 && position == 0) {
            holder.itemBaseViewModel.isDateVisible.set(true)
            holder.itemBaseViewModel.isSenderVisible.set(true)
        } else if (listSize > 0 && position < listSize) {
            val currItem = getItem(position)
            val currDate = EkoDateUtils.getRelativeDate(currItem?.getCreatedAt()?.millis ?: 0)

            val prevItem = getItem(position - 1)
            val prevDate = EkoDateUtils.getRelativeDate(prevItem?.getCreatedAt()?.millis ?: 0)

            if (currDate.isNotBlank() && prevDate.isNotBlank()) {
                holder.itemBaseViewModel.isDateVisible.set(currDate != prevDate)
            } else {
                holder.itemBaseViewModel.isDateVisible.set(false)
            }

            val currentName = currItem?.getUser()?.getDisplayName() ?: ""
            val nextName = prevItem?.getUser()?.getDisplayName() ?: ""
            if (currentName.isBlank() || nextName.isBlank()) {
                holder.itemBaseViewModel.isSenderVisible.set(true)
            } else {
                holder.itemBaseViewModel.isSenderVisible.set(currentName != nextName)
            }
        }

        if (firstCompletelyVisibleItem >= 0) {
            val firstItem = getItem(firstCompletelyVisibleItem)
            val date = EkoDateUtils.getRelativeDate(firstItem?.getCreatedAt()?.millis ?: 0)
            vmChat.stickyDate.set(date)
        }
    }

    companion object {

        private val MESSAGE_DIFF_CALLBACK = object : DiffUtil.ItemCallback<EkoMessage>() {

            override fun areItemsTheSame(oldItem: EkoMessage, newItem: EkoMessage): Boolean =
                oldItem.getMessageId() == newItem.getMessageId()

            override fun areContentsTheSame(oldItem: EkoMessage, newItem: EkoMessage): Boolean {
                return oldItem.getMessageId() == newItem.getMessageId()
                        && oldItem.isDeleted() == newItem.isDeleted()
                        && oldItem.getUser()?.getDisplayName() == newItem.getUser()?.getDisplayName()
                        && oldItem.getEditedAt() == newItem.getEditedAt()
                        && oldItem.getState() == newItem.getState()
            }

        }
    }

    interface ICustomViewHolder {

        fun getViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): EkoChatMessageBaseViewHolder?
    }
}