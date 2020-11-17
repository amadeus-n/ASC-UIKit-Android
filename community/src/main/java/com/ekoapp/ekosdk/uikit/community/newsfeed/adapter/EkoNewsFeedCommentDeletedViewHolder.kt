package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.common.readableFeedPostTime
import com.ekoapp.ekosdk.uikit.community.R


class EkoNewsFeedCommentDeletedViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView), EkoBaseRecyclerViewAdapter.IBinder<EkoComment> {

    private val deletionTime: TextView = itemView.findViewById(R.id.tvDeletionTime)

    override fun bind(data: EkoComment?, position: Int) {
        data?.let {
            deletionTime.text = data.getEditedAt()?.millis?.readableFeedPostTime(itemView.context)
        }
    }
}