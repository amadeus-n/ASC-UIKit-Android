package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.common.FileManager
import com.ekoapp.ekosdk.uikit.common.FileUtils
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.domain.model.FileAttachment
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostFileItemClickListener
import com.ekoapp.ekosdk.uikit.utils.EkoConstants.FILE_EXTENSION_SEPARATOR


open class EkoBaseFeedAttachmentViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView), EkoBaseRecyclerViewAdapter.IBinder<FileAttachment> {

    private var fileName: TextView = itemView.findViewById(R.id.tvFileName)
    private var fileSize: TextView = itemView.findViewById(R.id.tvFileSize)
    private val fileIcon: ImageView = itemView.findViewById(R.id.ivFileIcon)

    private var itemClickListener: IPostFileItemClickListener? = null

    constructor(itemView: View, itemClickListener: IPostFileItemClickListener?) : this(itemView) {
        this.itemClickListener = itemClickListener
    }

    override fun bind(data: FileAttachment?, position: Int) {
        data?.let {
            val fileNameTruncated = data.name
            setFileName(fileNameTruncated)
            setFileIcon(data)
            fileSize.text =
                FileUtils.humanReadableByteCount(data.size, true)
        }

        itemView.setOnClickListener {
            data?.let {
                itemClickListener?.onClickFileItem(it)
            }
        }
    }

    open fun getMaxCharacterLimit(): Int {
        return itemView.resources.getInteger(R.integer.maxCharacterNewsFeed)
    }

    private fun setFileIcon(data: FileAttachment) {
        val drawableRes = FileUtils.getFileIcon(data.mimeType)
        fileIcon.setImageDrawable(itemView.context.getDrawable(drawableRes))
    }

    private fun setFileName(originalName: String) {
        var fileNameTruncated = originalName
        if (originalName.length > getMaxCharacterLimit()) {

            val fileExtension: String = originalName.substringAfterLast(FILE_EXTENSION_SEPARATOR)
            val lastCharShown: Int = getMaxCharacterLimit() - fileExtension.length - 5
            fileNameTruncated =
                originalName.substringBeforeLast(FILE_EXTENSION_SEPARATOR)
                    .substring(0, lastCharShown) + "... ." + fileExtension

        }
        fileName.text = fileNameTruncated
    }
}
