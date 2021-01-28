package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.feed.EkoPostTarget
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.INewsFeedImageClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.INewsFeedItemActionListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostFileItemClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType

class EkoNewsFeedAdapter(
    private val timelineType: EkoTimelineType,
    private val itemActionListener: INewsFeedItemActionListener,
    private val imageClickListener: INewsFeedImageClickListener?,
    private val loadMoreFilesClickListener: EkoPostViewFileAdapter.ILoadMoreFilesClickListener?,
    private val fileItemClickListener: IPostFileItemClickListener?
) :
    EkoBaseRecyclerViewPagedAdapter<EkoPost>(diffCallBack) {

    override fun getLayoutId(position: Int, obj: EkoPost?): Int {
        return obj?.let { ekoPost ->
            if (!ekoPost.getChildren().isNullOrEmpty()) {
                when (ekoPost.getChildren().first().getData()) {
                    is EkoPost.Data.IMAGE -> {
                        R.layout.layout_news_feed_item_image
                    }
                    is EkoPost.Data.FILE -> {
                        R.layout.layout_news_feed_item_files
                    }
                    else -> {
                        R.layout.layout_news_feed_item_text
                    }
                }
            } else {
                R.layout.layout_news_feed_item_text
            }
        } ?: kotlin.run {
            R.layout.layout_news_feed_item_text
        }
    }

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.layout_news_feed_item_image -> NewsFeedItemImageViewHolder(
                view,
                itemActionListener,
                imageClickListener,
                timelineType
            )
            R.layout.layout_news_feed_item_files -> NewsFeedItemAttachmentViewHolder(
                view,
                itemActionListener,
                loadMoreFilesClickListener,
                fileItemClickListener, timelineType
            )
            else -> NewsFeedItemTextViewHolder(view, itemActionListener, timelineType)
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.getPostId().hashCode().toLong()
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
                        && oldItem.getEditedAt() == newItem.getEditedAt()
                        && oldItem.getChildren().size == newItem.getChildren().size
                        && oldItem.getLatestComments().size == newItem.getLatestComments().size
                        && areContentSame(oldItem.getLatestComments(), newItem.getLatestComments())

            }

        }

        private fun areContentSame(
            oldComments: List<EkoComment>, newComments: List<EkoComment>
        ): Boolean {
            for ((index, _) in oldComments.withIndex()) {
                if (oldComments[index].getData() != newComments[index].getData()
                    || oldComments[index].getReactionCount() != newComments[index].getReactionCount()
                    || oldComments[index].getEditedAt() != newComments[index].getEditedAt()
                ) {
                    return false
                }
            }
            return true
        }
    }

}
