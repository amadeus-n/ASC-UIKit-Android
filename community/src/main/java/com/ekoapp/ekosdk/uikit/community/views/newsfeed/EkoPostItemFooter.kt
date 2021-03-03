package com.ekoapp.ekosdk.uikit.community.views.newsfeed

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.feed.EkoPostTarget
import com.ekoapp.ekosdk.uikit.common.readableNumber
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.AmityItemPostFooterBinding
import com.ekoapp.ekosdk.uikit.community.newsfeed.adapter.EkoPostCommentAdapter
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.*
import com.ekoapp.ekosdk.uikit.components.EkoDividerItemDecor
import com.ekoapp.ekosdk.uikit.feed.settings.EkoFeedUISettings
import com.ekoapp.ekosdk.uikit.utils.EkoRecyclerViewItemDecoration
import kotlinx.android.synthetic.main.amity_item_post_footer.view.*


class EkoPostItemFooter : ConstraintLayout {

    private lateinit var mBinding: AmityItemPostFooterBinding
    private var newsFeedCommentAdapter: EkoPostCommentAdapter? = null

    private var commentItemClickListener: IPostCommentItemClickListener? = null
    private var shareListener: IPostActionShareListener? = null
    private var showMoreActionListener: IPostCommentShowMoreActionListener? = null
    private var showAllReplyListener: IPostCommentShowAllReplyListener? = null
    private var commentReplyClickListener: IPostCommentReplyClickListener? = null
    private var commentToExpand: String? = null
    private var readOnlyView: Boolean = false
    private var showRepliesComment: Boolean = false
    private var postId: String = ""
    var likeListener: IPostActionLikeListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.amity_item_post_footer, this, true)
        cbShare.setOnClickListener {
            shareListener?.onShareAction()
        }
    }

    private fun setNumberOfComments(commentCount: Int) {
        tvNumberOfComments.visibility = if (commentCount > 0) View.VISIBLE else View.GONE
        tvNumberOfComments.text = context.resources.getQuantityString(
            R.plurals.amity_feed_number_of_comments,
            commentCount,
            commentCount
        )
    }

    fun setPost(post: EkoPost) {
        postId = post.getPostId()
        setNumberOfLikes(post.getReactionCount())
        setNumberOfComments(post.getCommentCount())
        setUpLikeView(post)

        val target = post.getTarget()
        if (target is EkoPostTarget.COMMUNITY) {
            val community: EkoCommunity? = target.getCommunity()
            if (community != null) {
                readOnlyView = !community.isJoined()
                mBinding.readOnly = !community.isJoined()
            }
            if (newsFeedCommentAdapter?.readOnlyMode != readOnlyView) {
                newsFeedCommentAdapter?.readOnlyMode = readOnlyView
                newsFeedCommentAdapter?.notifyDataSetChanged()
            }
        } else {
            readOnlyView = false
            mBinding.readOnly = false
        }

        mBinding.showShareButton = isShowShareButton(post)
    }

    private fun setUpLikeView(feed: EkoPost) {
        val isLike = feed.getMyReactions().contains("like")
        refreshLikeView(isLike)
        setLikeClickListener(feed)
    }

    private fun setLikeClickListener(feed: EkoPost) {
        cbLike.setOnClickListener {
            val isLike = feed.getMyReactions().contains("like")
            refreshLikeView(!isLike)
            likeListener?.onLikeAction(!isLike)
        }
    }

    private fun refreshLikeView(isLike: Boolean) {
        cbLike.isChecked = isLike
        setLikeCheckboxText()
    }

    private fun isShowShareButton(post: EkoPost): Boolean {
        val targetPost = post.getTarget()
        if (targetPost is EkoPostTarget.USER && targetPost.getUser()
                ?.getUserId() == EkoClient.getUserId()
        ) {
            return EkoFeedUISettings.postSharingSettings.myFeedPostSharingTarget.isNotEmpty()
        } else if (targetPost is EkoPostTarget.USER && targetPost.getUser()
                ?.getUserId() != EkoClient.getUserId()
        ) {
            return EkoFeedUISettings.postSharingSettings.userFeedPostSharingTarget.isNotEmpty()
        } else {
            if (targetPost is EkoPostTarget.COMMUNITY) {
                targetPost.getCommunity()?.let {
                    return if (it.isPublic()) {
                        EkoFeedUISettings.postSharingSettings.publicCommunityPostSharingTarget.isNotEmpty()
                    } else {
                        EkoFeedUISettings.postSharingSettings.privateCommunityPostSharingTarget.isNotEmpty()
                    }
                }
            }
        }
        return false
    }

    private fun setNumberOfLikes(reactionCount: Int) {
        tvNumberOfLikes.visibility = if (reactionCount > 0) View.VISIBLE else View.GONE
        tvNumberOfLikes.text = context.resources.getQuantityString(
            R.plurals.amity_feed_number_of_likes,
            reactionCount,
            reactionCount.readableNumber()
        )
    }

    private fun setLikeCheckboxText() {
        if (cbLike.isChecked) {
            cbLike.setText(R.string.amity_liked)
        } else {
            cbLike.setText(R.string.amity_like)
        }
    }

    fun setItemClickListener(itemClickListener: IPostCommentItemClickListener?) {
        this.commentItemClickListener = itemClickListener
    }

    fun setShowAllReplyListener(showAllReplyListener: IPostCommentShowAllReplyListener?) {
        this.showAllReplyListener = showAllReplyListener
    }

    fun setShowMoreActionListener(showMoreActionListener: IPostCommentShowMoreActionListener?) {
        this.showMoreActionListener = showMoreActionListener
    }

    fun setFeedLikeActionListener(likeListener: IPostActionLikeListener) {
        this.likeListener = likeListener
    }

    fun setCommentClickListener(commentReplyClickListener: IPostCommentReplyClickListener) {
        this.commentReplyClickListener = commentReplyClickListener
    }

    fun setFeedShareActionListener(shareListener: IPostActionShareListener?) {
        this.shareListener = shareListener
    }

    fun setCommentActionListener(
        itemClickListener: IPostCommentItemClickListener?,
        showAllReplyListener: IPostCommentShowAllReplyListener?,
        showMoreActionListener: IPostCommentShowMoreActionListener?,
        commentReplyClickListener: IPostCommentReplyClickListener?
    ) {
        this.commentItemClickListener = itemClickListener
        this.showMoreActionListener = showMoreActionListener
        this.showAllReplyListener = showAllReplyListener
        this.commentReplyClickListener = commentReplyClickListener
    }

    private fun createAdapter() {
        newsFeedCommentAdapter = EkoPostCommentAdapter(
            commentItemClickListener,
            showAllReplyListener,
            showMoreActionListener,
            commentReplyClickListener,
            showRepliesComment,
            readOnlyView
        )
        val space8 = resources.getDimensionPixelSize(R.dimen.amity_padding_xs)
        val space16 = resources.getDimensionPixelSize(R.dimen.amity_padding_m1)
        val spaceItemDecoration = EkoRecyclerViewItemDecoration(space8, space16, 0, space16)
        val itemDecor = EkoDividerItemDecor(context)
        rvCommentFooter.addItemDecoration(spaceItemDecoration)
        rvCommentFooter.addItemDecoration(itemDecor)
        rvCommentFooter.layoutManager = LinearLayoutManager(context)
        rvCommentFooter.adapter = newsFeedCommentAdapter
        rvCommentFooter.visibility = GONE
        separator2.visibility = GONE
    }

    fun submitComments(commentList: PagedList<EkoComment>?) {
        if (newsFeedCommentAdapter == null) {
            createAdapter()
        }
        newsFeedCommentAdapter!!.submitList(commentList)

        if (newsFeedCommentAdapter!!.itemCount > 0) {
            rvCommentFooter.visibility = VISIBLE
            separator2.visibility = VISIBLE
        } else {
            rvCommentFooter.visibility = GONE
            separator2.visibility = GONE
        }
    }

    fun setPreExpandComment(commentToExpand: String?) {
        this.commentToExpand = commentToExpand
    }

    fun setShowRepliesComment(showRepliesComment: Boolean) {
        this.showRepliesComment = showRepliesComment
    }

    fun showViewAllComment(isVisible: Boolean) {
        mBinding.showViewAllComment = isVisible
    }

}
