package com.ekoapp.ekosdk.uikit.community.views.newsfeed

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.feed.EkoPostTarget
import com.ekoapp.ekosdk.uikit.common.readableNumber
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutNewsFeedItemFooterBinding
import com.ekoapp.ekosdk.uikit.community.newsfeed.adapter.EkoNewsFeedCommentAdapter
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.INewsFeedActionLikeListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.INewsFeedCommentItemClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.INewsFeedCommentShowAllReplyListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.INewsFeedCommentShowMoreActionListener
import com.ekoapp.ekosdk.uikit.components.EkoDividerItemDecor
import com.ekoapp.ekosdk.uikit.utils.EkoRecyclerViewItemDecoration
import kotlinx.android.synthetic.main.layout_news_feed_item_footer.view.*


class EkoNewsFeedItemFooter : ConstraintLayout {

    private lateinit var mBinding: LayoutNewsFeedItemFooterBinding
    private var newsFeedCommentAdapter: EkoNewsFeedCommentAdapter? = null
    private var commentItemClickListener: INewsFeedCommentItemClickListener? = null
    private var showMoreActionListener: INewsFeedCommentShowMoreActionListener? = null
    private var showAllReplyListener: INewsFeedCommentShowAllReplyListener? = null
    private var commentToExpand: String? = null
    private var readOnlyView: Boolean = false
    private var feedId: String = ""
    var likeListener: INewsFeedActionLikeListener? = null

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
            DataBindingUtil.inflate(inflater, R.layout.layout_news_feed_item_footer, this, true)

        cbLike.setOnCheckedChangeListener { buttonView, isChecked ->
            setLikeCheckboxText()
            likeListener?.onLikeAction(
                isChecked
            )
        }
    }

    private fun setNumberOfComments(commentCount: Int) {
        tvNumberOfComments.visibility = if (commentCount > 0) View.VISIBLE else View.GONE
        tvNumberOfComments.text = context.resources.getQuantityString(
            R.plurals.feed_number_of_comments,
            commentCount,
            commentCount
        )
    }

    //TODO move to data binding
    fun setFeed(feed: EkoPost) {
        feedId = feed.getPostId()
        setNumberOfLikes(feed.getReactionCount())
        setNumberOfComments(feed.getCommentCount())
        cbLike.isChecked = feed.getMyReactions().contains("like")
        setLikeCheckboxText()
        val target = feed.getTarget()
        if (target is EkoPostTarget.COMMUNITY) {
            val community: EkoCommunity? = target.getCommunity()
            if (community != null) {
                readOnlyView = !community.isJoined()
                mBinding.readOnly = !community.isJoined()
            }
            newsFeedCommentAdapter?.readOnlyMode = readOnlyView
            newsFeedCommentAdapter?.notifyDataSetChanged()
        } else {
            readOnlyView = false
            mBinding.readOnly = false
        }
    }

    private fun setNumberOfLikes(reactionCount: Int) {
        tvNumberOfLikes.visibility = if (reactionCount > 0) View.VISIBLE else View.GONE
        tvNumberOfLikes.text = context.resources.getQuantityString(
            R.plurals.feed_number_of_likes,
            reactionCount,
            reactionCount.readableNumber()
        )
    }

    private fun setLikeCheckboxText() {
        if (cbLike.isChecked) {
            cbLike.setText(R.string.liked)
        } else {
            cbLike.setText(R.string.like)
        }
    }

    fun setItemClickListener(itemClickListener: INewsFeedCommentItemClickListener?) {
        this.commentItemClickListener = itemClickListener
    }

    fun setShowAllReplyListener(showAllReplyListener: INewsFeedCommentShowAllReplyListener?) {
        this.showAllReplyListener = showAllReplyListener
    }

    fun setShowMoreActionListener(showMoreActionListener: INewsFeedCommentShowMoreActionListener?) {
        this.showMoreActionListener = showMoreActionListener
    }

    fun setFeedLikeActionListener(likeListener: INewsFeedActionLikeListener) {
        this.likeListener = likeListener
    }

    fun setCommentActionListener(
        itemClickListener: INewsFeedCommentItemClickListener?,
        showAllReplyListener: INewsFeedCommentShowAllReplyListener?,
        showMoreActionListener: INewsFeedCommentShowMoreActionListener?
    ) {
        this.commentItemClickListener = itemClickListener
        this.showMoreActionListener = showMoreActionListener
        this.showAllReplyListener = showAllReplyListener
    }

    private fun initEkoPostCommentRecyclerview() {
        newsFeedCommentAdapter = EkoNewsFeedCommentAdapter(
            commentItemClickListener,
            showAllReplyListener,
            showMoreActionListener,
            commentToExpand,
            readOnlyView
        )
        val space8 = resources.getDimensionPixelSize(R.dimen.eight)
        val space16 = resources.getDimensionPixelSize(R.dimen.sixteen)
        val spaceItemDecoration = EkoRecyclerViewItemDecoration(space8, space16, 0, space16)
        val itemDecor = EkoDividerItemDecor(context)
        rvCommentFooter.addItemDecoration(spaceItemDecoration)
        rvCommentFooter.addItemDecoration(itemDecor)
        (rvCommentFooter.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        rvCommentFooter.layoutManager = LinearLayoutManager(context)
        rvCommentFooter.adapter = newsFeedCommentAdapter
        rvCommentFooter.visibility = GONE
        separator2.visibility = GONE
    }

    fun submitComments(pagedList: List<EkoComment>) {
        if (newsFeedCommentAdapter == null) {
            initEkoPostCommentRecyclerview()
        }
        newsFeedCommentAdapter!!.submitList(pagedList)

        if (newsFeedCommentAdapter!!.itemCount > 0) {
            rvCommentFooter.visibility = VISIBLE
            separator2.visibility = VISIBLE
        } else {
            rvCommentFooter.visibility = GONE
            separator2.visibility = GONE
        }
    }

    fun scrollToBottomComments() {
        if (newsFeedCommentAdapter != null)
            rvCommentFooter.smoothScrollToPosition(newsFeedCommentAdapter!!.itemCount - 1)
    }

    fun notifyCommentAdded() {
        newsFeedCommentAdapter?.notifyItemInserted(newsFeedCommentAdapter!!.itemCount)
    }

    fun notifyCommentDeleted(position: Int) {
        newsFeedCommentAdapter?.notifyItemChanged(position)
    }

    fun notifyCommentUpdated(position: Int) {
        newsFeedCommentAdapter?.notifyItemChanged(position)
    }

    fun setPreExpandComment(commentToExpand: String?) {
        this.commentToExpand = commentToExpand
    }

    fun enableReadOnlyView() {
        readOnlyView = true
        mBinding.readOnly = true
    }

}
