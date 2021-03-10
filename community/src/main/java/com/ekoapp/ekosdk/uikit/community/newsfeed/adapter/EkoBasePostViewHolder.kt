package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.comment.EkoCommentReference
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.common.toPagedList
import com.ekoapp.ekosdk.uikit.common.views.text.EkoExpandableTextView
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.*
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType
import com.ekoapp.ekosdk.uikit.community.views.newsfeed.EkoPostItemFooter
import com.ekoapp.ekosdk.uikit.community.views.newsfeed.EkoPostItemHeader
import com.ekoapp.ekosdk.user.EkoUser

private const val MAXIMUM_COMMENTS_TO_SHOW = 2

open class EkoBasePostViewHolder(
    itemView: View,
    private val timelineType: EkoTimelineType
) : RecyclerView.ViewHolder(itemView),
    EkoBaseRecyclerViewPagedAdapter.Binder<EkoPost> {

    private var itemActionLister: IPostItemActionListener? = null
    private var useHeaderLayout = false
    private var useFooterLayout = false
    private var ekoHeader: EkoPostItemHeader? = null
    private var ekoFooter: EkoPostItemFooter? = null

    internal fun enableHeaderLayout(header: EkoPostItemHeader) {
        useHeaderLayout = true
        ekoHeader = header
    }

    internal fun enableFooterLayout(footer: EkoPostItemFooter) {
        useFooterLayout = true
        ekoFooter = footer
    }

    internal fun setItemActionListener(listener: IPostItemActionListener) {
        itemActionLister = listener
    }

    @CallSuper
    override fun bind(data: EkoPost?, position: Int) {
        if (data != null) {
            if (useHeaderLayout) {
                setHeaderLayout(data, position)
            }

            if (useFooterLayout) {
                setFooterLayout(data, position)
            }
        }

    }

    internal fun setPostText(data: EkoPost, position: Int, showCompleteText: Boolean) {
        val tvPost = itemView.findViewById<EkoExpandableTextView>(R.id.tvFeed)
        tvPost.text = (data.getData() as? EkoPost.Data.TEXT)?.getText() ?: ""
        if (showCompleteText) {
            tvPost.showCompleteText()
            tvPost.tag = tvPost.getVisibleLineCount()
        }

        if (tvPost.tag != tvPost.getVisibleLineCount()) {
            tvPost.forceLayout()
            tvPost.tag = tvPost.getVisibleLineCount()
        }

        tvPost.isVisible = tvPost.text.isNotEmpty()

        tvPost.setExpandOnlyOnReadMoreClick(true)

        tvPost.setOnClickListener {
            if (tvPost.isReadMoreClicked()) {
                tvPost.showCompleteText()
                tvPost.tag = tvPost.getVisibleLineCount()
            } else {
                itemActionLister?.onClickItem(data.getPostId(), position)
            }

        }
    }

    private fun setHeaderLayout(data: EkoPost, position: Int) {

        ekoHeader?.setFeed(data, timelineType)

        ekoHeader?.setNewsFeedActionAvatarClickListener(object :
            IPostActionAvatarClickListener {
            override fun onClickUserAvatar(user: EkoUser) {
                itemActionLister?.onClickUserAvatar(data, user, position)
            }

        })

        ekoHeader?.setNewsFeedActionCommunityClickListener(object :
            IPostActionCommunityClickListener {

            override fun onClickCommunity(community: EkoCommunity) {
                itemActionLister?.onClickCommunity(community)
            }

        })

        ekoHeader?.getFeedActionButton()?.setOnClickListener {
            itemActionLister?.onFeedAction(data, position)
        }
    }

    private fun setFooterLayout(data: EkoPost, position: Int) {
        ekoFooter?.setPost(data)
        ekoFooter?.setFeedLikeActionListener(object : IPostActionLikeListener {
            override fun onLikeAction(liked: Boolean) {
                itemActionLister?.onLikeAction(liked, data, position)
            }

        })

        ekoFooter?.setFeedShareActionListener(object : IPostActionShareListener {
            override fun onShareAction() {
                itemActionLister?.onShareAction(data, position)
            }
        })

        ekoFooter?.setItemClickListener(object : IPostCommentItemClickListener {
            override fun onClickItem(comment: EkoComment, position: Int) {
                val postId =
                    (comment.getReference() as? EkoCommentReference.Post)?.getPostId() ?: ""
                itemActionLister?.onClickItem(postId, position)
            }

            override fun onClickAvatar(user: EkoUser) {
                itemActionLister?.onClickUserAvatar(data, user, position)
            }
        })

        ekoFooter?.setCommentClickListener(object : IPostCommentReplyClickListener {
            override fun onClickCommentReply(comment: EkoComment, position: Int) {
                val postId =
                    (comment.getReference() as? EkoCommentReference.Post)?.getPostId() ?: ""
                itemActionLister?.onClickItem(postId, position)
            }
        })

        ekoFooter?.setShowAllReplyListener(object :
            IPostCommentShowAllReplyListener {
            override fun onClickShowAllReplies(comment: EkoComment, position: Int) {
                itemActionLister?.showAllReply(data, comment, position)
            }

        })

        ekoFooter?.setShowMoreActionListener(object :
            IPostCommentShowMoreActionListener {
            override fun onClickNewsFeedCommentShowMoreAction(
                comment: EkoComment,
                position: Int
            ) {
                itemActionLister?.onCommentAction(data, comment, position)
            }

        })

        val latestComments = data.getLatestComments()
        if (latestComments.isNotEmpty()) {
            ekoFooter?.submitComments(
                latestComments
                    .take(MAXIMUM_COMMENTS_TO_SHOW)
                    .toPagedList(MAXIMUM_COMMENTS_TO_SHOW)
            )
        } else {
            ekoFooter?.submitComments(null)
        }

        ekoFooter?.showViewAllComment(data.getLatestComments().size > MAXIMUM_COMMENTS_TO_SHOW)

        ekoFooter?.setOnClickListener {
            itemActionLister?.onClickItem(data.getPostId(), position)
        }
    }

}