package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostFileItemClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostImageClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType
import com.ekoapp.ekosdk.uikit.feed.settings.EkoDefaultPostViewHolders
import com.ekoapp.ekosdk.uikit.feed.settings.EkoFeedUISettings

class EkoPostDetailAdapter(
    private var postList: List<EkoPost>, private val imageClickListener: IPostImageClickListener,
    private val fileItemClickListener: IPostFileItemClickListener
) : RecyclerView.Adapter<EkoBasePostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EkoBasePostViewHolder {
        val viewHolder = EkoFeedUISettings.getViewHolder(viewType)
        val view =
            LayoutInflater.from(parent.context).inflate(viewHolder.getLayoutId(), parent, false)
        val result = viewHolder.createViewHolder(view, EkoTimelineType.GLOBAL)
        when (result) {
            is PostItemImageViewHolder -> {
                result.setImageClickListener(imageClickListener)
                result.showCompleteFeedText(true)
            }
            is PostItemAttachmentViewHolder -> {
                result.setFileItemClickListener(fileItemClickListener)
                result.showCompleteText(true)
                result.isCollapsible(false)
            }
            is PostItemTextViewHolder -> {
                result.showCompleteText(true)
            }
        }
        return result
    }

    override fun onBindViewHolder(holder: EkoBasePostViewHolder, position: Int) {
        holder.bind(postList[position], position)
    }

    override fun getItemCount(): Int = postList.size

    override fun getItemViewType(position: Int): Int {
        return postList[position].let { ekoPost ->
            if (!ekoPost.getChildren().isNullOrEmpty()) {
                when (ekoPost.getChildren().first().getData()) {
                    is EkoPost.Data.IMAGE -> {
                        EkoDefaultPostViewHolders.imageViewHolder.getDataType().hashCode()
                    }
                    is EkoPost.Data.FILE -> {
                        EkoDefaultPostViewHolders.fileViewHolder.getDataType().hashCode()
                    }
                    else -> {
                        EkoDefaultPostViewHolders.textViewHolder.getDataType().hashCode()
                    }
                }
            } else {
                when (ekoPost.getData()) {
                    is EkoPost.Data.TEXT -> {
                        EkoDefaultPostViewHolders.textViewHolder.getDataType().hashCode()
                    }
                    is EkoPost.Data.CUSTOM -> {
                        (ekoPost.getData() as EkoPost.Data.CUSTOM).getDataType().hashCode()
                    }
                    else -> {
                        EkoDefaultPostViewHolders.unknownViewHolder.getDataType().hashCode()
                    }
                }
            }
        }
    }

    fun submitList(postList: List<EkoPost>) {
        this.postList = postList
        notifyDataSetChanged()
    }
}