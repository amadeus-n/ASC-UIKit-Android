package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.file.EkoFile
import com.ekoapp.ekosdk.uikit.base.SpacesItemDecoration
import com.ekoapp.ekosdk.uikit.common.FileUtils
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.domain.model.FileAttachment
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostFileItemClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.model.FileUploadState
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType

class PostItemAttachmentViewHolder(
    itemView: View,
    timelineType: EkoTimelineType
) : EkoBasePostViewHolder(itemView, timelineType) {
    private val rvAttachment = itemView.findViewById<RecyclerView>(R.id.rvAttachment)
    private val space = itemView.context.resources.getDimensionPixelSize(R.dimen.amity_padding_xs)
    private val itemDecor = SpacesItemDecoration(0, 0, 0, space)
    private var collapsible = true

    private var loadMoreFilesClickListener: EkoPostViewFileAdapter.ILoadMoreFilesClickListener? =
        null
    private var fileItemClickListener: IPostFileItemClickListener? = null

    private var showCompleteText = false

    internal fun setLoadMoreFilesListener(listener: EkoPostViewFileAdapter.ILoadMoreFilesClickListener) {
        loadMoreFilesClickListener = listener
    }

    internal fun setFileItemClickListener(listener: IPostFileItemClickListener) {
        fileItemClickListener = listener
    }

    internal fun showCompleteText(value: Boolean) {
        showCompleteText = value
    }

    internal fun isCollapsible(value: Boolean) {
        collapsible = value
    }

    override fun bind(data: EkoPost?, position: Int) {
        super.bind(data, position)
        data?.let { ekoPost ->
            setPostText(data, position, showCompleteText)
            val files = mutableListOf<EkoFile>()
            if (!ekoPost.getChildren().isNullOrEmpty()) {
                ekoPost.getChildren().forEach {
                    when (val postData = it.getData()) {
                        is EkoPost.Data.FILE -> {
                            postData.getFile()?.let { ekoFile ->
                                files.add(ekoFile)
                            }
                        }
                    }
                }
                initAttachments(ekoPost, files)
            }
        }
    }

    private fun initAttachments(data: EkoPost, files: List<EkoFile>) {
        val adapter =
            EkoPostViewFileAdapter(loadMoreFilesClickListener, fileItemClickListener, data, collapsible)
        rvAttachment.removeItemDecoration(itemDecor)
        rvAttachment.addItemDecoration(itemDecor)
        rvAttachment.layoutManager = LinearLayoutManager(itemView.context)
        rvAttachment.adapter = adapter
        adapter.submitList(mapEkoFilesToFileAttachments(files))
    }

    private fun mapEkoFilesToFileAttachments(ekoFile: List<EkoFile>): List<FileAttachment> {
        return ekoFile.map {
            val fileSize = it.getFileSize()?.toLong() ?: 0L
            FileAttachment(
                it.getFileId(),
                null,
                it.getFileName() ?: "",
                fileSize,
                Uri.parse(it.getUrl()),
                FileUtils.humanReadableByteCount(fileSize, true)!!,
                it.getMimeType() ?: "",
                FileUploadState.COMPLETE,
                100
            )
        }
    }
}