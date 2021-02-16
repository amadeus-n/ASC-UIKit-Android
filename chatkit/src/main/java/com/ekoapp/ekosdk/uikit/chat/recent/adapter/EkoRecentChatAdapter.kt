package com.ekoapp.ekosdk.uikit.chat.recent.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.channel.EkoChannel
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.home.callback.IRecentChatItemClickListener

class EkoRecentChatAdapter : EkoBaseRecyclerViewPagedAdapter<EkoChannel>(diffCallBack) {
    private var recentChatItemClickListener: IRecentChatItemClickListener? = null

    override fun getLayoutId(position: Int, obj: EkoChannel?): Int =
        R.layout.amity_item_recent_message

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder =
        EkoRecentChatViewHolder(view, recentChatItemClickListener)

    fun setCommunityChatItemClickListener(listener: IRecentChatItemClickListener?) {
        this.recentChatItemClickListener = listener
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<EkoChannel>() {
            override fun areItemsTheSame(oldItem: EkoChannel, newItem: EkoChannel): Boolean =
                oldItem.getChannelId() == newItem.getChannelId()


            override fun areContentsTheSame(oldItem: EkoChannel, newItem: EkoChannel): Boolean =
                oldItem == newItem
        }
    }
}
