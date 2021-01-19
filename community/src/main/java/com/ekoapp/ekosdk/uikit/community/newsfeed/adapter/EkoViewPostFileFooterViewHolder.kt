package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.domain.model.FileAttachment

class EkoViewPostFileFooterViewHolder(
        itemView: View,
        private val loadMoreClickListener: EkoPostViewFileAdapter.ILoadMoreFilesClickListener?,
        private val newsFeed: EkoPost?
) : RecyclerView.ViewHolder(itemView), EkoBaseRecyclerViewAdapter.IBinder<FileAttachment> {
    private val tvLoadMoreFiles = itemView.findViewById<TextView>(R.id.tvLoadMoreFiles)

    override fun bind(data: FileAttachment?, position: Int) {
        tvLoadMoreFiles.setOnClickListener {
            if (loadMoreClickListener != null && newsFeed != null) {
                loadMoreClickListener.loadMoreFiles(newsFeed)
            }
        }
    }

}
