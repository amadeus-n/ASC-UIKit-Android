package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.domain.model.FileAttachment
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.ICreatePostFileActionListener

class EkoCreatePostFileAdapter(val listener: ICreatePostFileActionListener?) :
        EkoBaseFeedAttachmentAdapter() {

    override fun getLayoutId(position: Int, obj: FileAttachment?): Int {
        return R.layout.layout_create_post_file_item
    }

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return EkoCreatePostFileViewHolder(view, listener)
    }
}