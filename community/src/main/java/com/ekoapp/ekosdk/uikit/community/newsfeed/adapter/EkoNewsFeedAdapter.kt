package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.feed.EkoPostTarget
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostFileItemClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostImageClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostItemActionListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType
import com.ekoapp.ekosdk.uikit.community.views.newsfeed.EkoPostItemFooter
import com.ekoapp.ekosdk.uikit.community.views.newsfeed.EkoPostItemHeader
import com.ekoapp.ekosdk.uikit.feed.settings.EkoDefaultPostViewHolders
import com.ekoapp.ekosdk.uikit.feed.settings.EkoFeedUISettings

class EkoNewsFeedAdapter(
    private val timelineType: EkoTimelineType,
    private val itemActionListener: IPostItemActionListener,
    private val imageClickListener: IPostImageClickListener,
    private val loadMoreFilesClickListener: EkoPostViewFileAdapter.ILoadMoreFilesClickListener,
    private val fileItemClickListener: IPostFileItemClickListener
) : PagedListAdapter<EkoPost, EkoBasePostViewHolder>(diffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EkoBasePostViewHolder {
        val viewHolder = EkoFeedUISettings.getViewHolder(viewType)
        val templateView = LayoutInflater.from(parent.context).inflate(R.layout.amity_news_feed_template, parent, false)
        val bodyContainer: ConstraintLayout = templateView.findViewById(R.id.containerBody)

        val layoutId = viewHolder.getLayoutId()
        val bodyView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        bodyContainer.addView(bodyView)

        val result = viewHolder.createViewHolder(templateView, timelineType)
        when (result) {
            is PostItemImageViewHolder -> result.setImageClickListener(imageClickListener)
            is PostItemAttachmentViewHolder -> {
                result.setFileItemClickListener(fileItemClickListener)
                result.setLoadMoreFilesListener(loadMoreFilesClickListener)
            }
        }
        if (viewHolder.useEkoHeader()) {
            val headerView = templateView.findViewById<EkoPostItemHeader>(R.id.newsFeedHeader)
            headerView.visibility = View.VISIBLE
            result.enableHeaderLayout(headerView)
        }
        if (viewHolder.useEkoFooter()) {
            val footerView = templateView.findViewById<EkoPostItemFooter>(R.id.newsFeedFooter)
            footerView.visibility = View.VISIBLE
            result.enableFooterLayout(footerView)
        }
        result.setItemActionListener(itemActionListener)
        return result
    }

    override fun onBindViewHolder(holder: EkoBasePostViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.let { ekoPost ->
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
        } ?: kotlin.run {
            EkoDefaultPostViewHolders.noDataViewHolder.getDataType().hashCode()
        }

    }

    override fun getItemId(position: Int): Long {
        getItem(position)?.let {
            return it.getPostId().hashCode().toLong()
        } ?: kotlin.run {
            return System.currentTimeMillis()
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<EkoPost>() {
            override fun areItemsTheSame(oldItem: EkoPost, newItem: EkoPost): Boolean {
                return oldItem.getPostId() == newItem.getPostId()
            }

            override fun areContentsTheSame(oldItem: EkoPost, newItem: EkoPost): Boolean {
                val oldItemIsJoined =
                    (oldItem.getTarget() as? EkoPostTarget.COMMUNITY)?.getCommunity()?.isJoined()
                val newItemIsJoined =
                    (newItem.getTarget() as? EkoPostTarget.COMMUNITY)?.getCommunity()?.isJoined()

                return oldItem.getPostId() == newItem.getPostId()
                        && oldItem.getPostedUser()?.getDisplayName() == newItem.getPostedUser()
                    ?.getDisplayName()
                        && oldItem.getPostedUser()?.getRoles() == newItem.getPostedUser()
                    ?.getRoles()
                        && oldItem.getPostedUser()?.getAvatar()?.getUrl() == newItem.getPostedUser()
                    ?.getAvatar()?.getUrl()
                        && oldItemIsJoined == newItemIsJoined
                        && oldItem.getCommentCount() == newItem.getCommentCount()
                        && oldItem.getReactionCount() == newItem.getReactionCount()
                        && oldItem.getChildren().size == newItem.getChildren().size
                        && oldItem.getLatestComments().size == newItem.getLatestComments().size
                        && oldItem.getMyReactions() == newItem.getMyReactions()
                        && isTargetTheSame(oldItem, newItem)
                        && isDataTheSame(oldItem, newItem)
                        && areLatestCommentsTheSame(
                    oldItem.getLatestComments(),
                    newItem.getLatestComments()
                )
            }

        }

        private fun isDataTheSame(oldItem: EkoPost, newItem: EkoPost): Boolean {
            when (oldItem.getData()) {
                is EkoPost.Data.TEXT -> {
                    val oldText = (oldItem.getData() as? EkoPost.Data.TEXT)?.getText()
                    val newText = (newItem.getData() as? EkoPost.Data.TEXT)?.getText()
                    return oldText == newText
                }
                is EkoPost.Data.IMAGE -> {
                    val oldImageUrl =
                        (oldItem.getData() as? EkoPost.Data.IMAGE)?.getImage()?.getUrl()
                    val newImageUrl =
                        (newItem.getData() as? EkoPost.Data.IMAGE)?.getImage()?.getUrl()
                    return oldImageUrl == newImageUrl
                }
                is EkoPost.Data.FILE -> {
                    val oldFileUrl = (oldItem.getData() as? EkoPost.Data.FILE)?.getFile()?.getUrl()
                    val newFileUrl = (newItem.getData() as? EkoPost.Data.FILE)?.getFile()?.getUrl()
                    return oldFileUrl == newFileUrl
                }
                is EkoPost.Data.CUSTOM -> {
                    val oldCustomData = (oldItem.getData() as? EkoPost.Data.CUSTOM)?.getRawJson()
                    val newCustomData = (newItem.getData() as? EkoPost.Data.CUSTOM)?.getRawJson()
                    return oldCustomData?.equals(newCustomData) ?: false
                }
                else -> return false
            }
        }

        private fun isTargetTheSame(oldItem: EkoPost, newItem: EkoPost): Boolean {
            when (oldItem.getTarget()) {
                is EkoPostTarget.COMMUNITY -> {
                    val oldCommunityDisplayName = (oldItem.getTarget() as? EkoPostTarget.COMMUNITY)
                        ?.getCommunity()?.getDisplayName()
                    val newCommunityDisplayName = (newItem.getTarget() as? EkoPostTarget.COMMUNITY)
                        ?.getCommunity()?.getDisplayName()
                    return oldCommunityDisplayName == newCommunityDisplayName
                }
                is EkoPostTarget.USER -> {
                    val oldUserDisplayName =
                        (oldItem.getTarget() as? EkoPostTarget.USER)?.getUser()?.getDisplayName()
                    val newUserDisplayName =
                        (newItem.getTarget() as? EkoPostTarget.USER)?.getUser()?.getDisplayName()
                    return oldUserDisplayName == newUserDisplayName
                }
                else -> return false
            }
        }

        private fun areLatestCommentsTheSame(
            oldComments: List<EkoComment>,
            newComments: List<EkoComment>
        ): Boolean {
            for (index in oldComments.indices) {
                if (!isCommentDataTheSame(oldComments[index], newComments[index])
                    || oldComments[index].getReactionCount() != newComments[index].getReactionCount()
                ) {
                    return false
                }
            }
            return true
        }

        private fun isCommentDataTheSame(oldItem: EkoComment, newItem: EkoComment): Boolean {
            return when (oldItem.getData()) {
                is EkoComment.Data.TEXT -> {
                    val oldDataItem = (oldItem.getData() as? EkoComment.Data.TEXT)?.getText()
                    val newDataItem = (newItem.getData() as? EkoComment.Data.TEXT)?.getText()
                    oldDataItem == newDataItem
                }
                else -> false
            }
        }
    }

}
