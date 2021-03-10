package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostCommentItemClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostCommentReplyClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostCommentShowAllReplyListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostCommentShowMoreActionListener


class EkoPostCommentAdapter(
    val itemClickListener: IPostCommentItemClickListener?,
    private val showAllReplyListener: IPostCommentShowAllReplyListener?,
    private val showMoreActionListener: IPostCommentShowMoreActionListener?,
    private val commentReplyClickListener: IPostCommentReplyClickListener?,
    private val showRepliesComment: Boolean = false,
    var readOnlyMode: Boolean = false,
    private val loaderMap: MutableMap<String, EkoCommentReplyLoader> = mutableMapOf()
) : EkoBaseRecyclerViewPagedAdapter<EkoComment>(PostCommentDiffUtil(loaderMap)) {

    override fun getLayoutId(position: Int, obj: EkoComment?): Int {
        return if (obj?.isDeleted() == true) {
            val isReplyComment = obj.getParentId() != null
            if (isReplyComment) {
                R.layout.amity_item_deleted_news_feed_reply
            } else {
                R.layout.amity_item_deleted_comment_post
            }
        } else {
            R.layout.amity_item_comment_post
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        setReadOnlyMode(holder)
        super.onBindViewHolder(holder, position)
    }

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.amity_item_deleted_comment_post -> {
                EkoPostCommentDeletedViewHolder(view)
            }
            R.layout.amity_item_deleted_news_feed_reply -> {
                EkoPostReplyDeletedViewHolder(view)
            }
            else -> {
                EkoPostCommentViewHolder(
                    view,
                    itemClickListener,
                    showAllReplyListener,
                    showMoreActionListener,
                    commentReplyClickListener,
                    showRepliesComment,
                    loaderMap,
                    readOnlyMode
                )
            }
        }
    }

    private fun setReadOnlyMode(holder: RecyclerView.ViewHolder) {
        (holder as? EkoPostCommentViewHolder)?.readOnlyMode = readOnlyMode
    }

    class PostCommentDiffUtil(private val loaderMap: Map<String, EkoCommentReplyLoader>) :
        DiffUtil.ItemCallback<EkoComment>() {

        private fun shouldIgnoreReplies(comment: EkoComment): Boolean {
            return loaderMap.containsKey(comment.getCommentId())
        }

        override fun areItemsTheSame(oldItem: EkoComment, newItem: EkoComment): Boolean {
            return oldItem.getCommentId() == newItem.getCommentId()
        }

        override fun areContentsTheSame(oldItem: EkoComment, newItem: EkoComment): Boolean {
            return oldItem.getCommentId() == newItem.getCommentId()
                    && oldItem.isDeleted() == newItem.isDeleted()
                    && oldItem.getUser()
                ?.getDisplayName() == newItem.getUser()?.getDisplayName()
                    && oldItem.getUser()?.getAvatar()
                ?.getUrl() == newItem.getUser()?.getAvatar()?.getUrl()
                    && oldItem.getReactionCount() == newItem.getReactionCount()
                    && isDataTheSame(oldItem, newItem)
                    && (shouldIgnoreReplies(newItem)
                    || areRepliesTheSame(oldItem.getLatestReplies(), newItem.getLatestReplies()))
        }

        private fun isDataTheSame(oldItem: EkoComment, newItem: EkoComment): Boolean {
            return when (oldItem.getData()) {
                is EkoComment.Data.TEXT -> {
                    val oldDataItem = (oldItem.getData() as? EkoComment.Data.TEXT)?.getText()
                    val newDataItem = (newItem.getData() as? EkoComment.Data.TEXT)?.getText()
                    oldDataItem == newDataItem
                }
                else -> false
            }
        }

        private fun areRepliesTheSame(
            oldReplies: List<EkoComment>,
            newReplies: List<EkoComment>
        ): Boolean {
            if (oldReplies.size != newReplies.size) {
                return false
            } else {
                for (index in oldReplies.indices) {
                    val isDifferent =
                        !areContentsTheSame(oldReplies[index], newReplies[index])
                    if (isDifferent) {
                        return false
                    }
                }
            }
            return true
        }

    }

}