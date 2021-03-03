package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.common.toPagedList
import com.ekoapp.ekosdk.uikit.community.databinding.AmityItemCommentPostBinding
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostCommentItemClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostCommentReplyClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostCommentShowAllReplyListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostCommentShowMoreActionListener
import com.ekoapp.ekosdk.uikit.community.views.newsfeed.EkoPostCommentView
import com.ekoapp.rxlifecycle.extension.untilLifecycleEnd
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.amity_item_comment_news_feed.view.*

const val MAXIMUM_REPLIES_COMMENTS_TO_SHOW = 3

class EkoPostCommentViewHolder(
    itemView: View,
    private val itemClickListener: IPostCommentItemClickListener?,
    private val showAllReplyListener: IPostCommentShowAllReplyListener?,
    private val showMoreActionListener: IPostCommentShowMoreActionListener?,
    private val commentReplyClickListener: IPostCommentReplyClickListener?,
    private val showRepliesComment: Boolean = false,
    private val loaderMap: MutableMap<String, EkoCommentReplyLoader>,
    var readOnlyMode: Boolean
) : RecyclerView.ViewHolder(itemView), EkoBaseRecyclerViewPagedAdapter.Binder<EkoComment> {
    private val binding: AmityItemCommentPostBinding? = DataBindingUtil.bind(itemView)
    private var newsFeedCommentAdapter: EkoPostCommentAdapter? = null
    private var commentLoader: EkoCommentReplyLoader? = null

    override fun bind(data: EkoComment?, position: Int) {
        commentLoader = null
        data?.let { comment ->
            initialListener(comment, position)

            binding?.ekoNewsFeedComment?.setComment(comment)
            binding?.ekoNewsFeedComment?.setReadOnlyMode(readOnlyMode)

            if (newsFeedCommentAdapter == null) {
                createAdapter()
            }

            setupViewRepliesButton(comment)
            setupRepliesView(comment)

            addCommentActionListener(comment, position)
            addItemClickListener(comment, position)

        } ?: kotlin.run {
            // Do nothing
        }
    }

    private fun setupViewRepliesButton(comment: EkoComment) {
        if (comment.getChildrenNumber() > 0 && !showRepliesComment) {
            binding?.ekoNewsFeedComment?.setShowViewRepliesButton(true)
        } else {
            binding?.ekoNewsFeedComment?.setShowViewRepliesButton(false)
        }
    }

    private fun isReplyComment(comment: EkoComment): Boolean {
        return comment.getParentId() != null
    }

    private fun initialListener(comment: EkoComment, position: Int) {
        binding?.ekoNewsFeedComment?.setOnExpandClickListener(View.OnClickListener {
            itemClickListener?.onClickItem(comment, position)
        })

        binding?.viewMoreRepliesContainer?.setOnClickListener {
            binding.showProgressBar = true
            binding.showViewMoreRepliesButton = false
            if (loaderMap[comment.getCommentId()] == null) {
                commentLoader = EkoCommentReplyLoader(comment)
                loaderMap[comment.getCommentId()] = commentLoader!!
            }

            commentLoader!!.getComments()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .untilLifecycleEnd(view = itemView)
                .subscribe({ comments ->
                    showReplies(comments)
                }, {
                })

            commentLoader!!.showLoadMoreButton()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .untilLifecycleEnd(view = itemView)
                .subscribe({ shouldShow ->
                    val loadMore = shouldShow && showRepliesComment
                    binding.showViewMoreRepliesButton = loadMore
                }, {

                })


            commentLoader!!.load()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .untilLifecycleEnd(view = itemView)
                .subscribe({
                    binding.showProgressBar = false
                }, {

                })
        }
    }


    private fun setupRepliesView(comment: EkoComment) {
        if (isReplyComment(comment) || !showRepliesComment) {
            showReplies(emptyList())
            return
        }

        if (loaderMap[comment.getCommentId()] == null) {
            val shouldShowReplies =
                comment.getLatestReplies().size > MAXIMUM_REPLIES_COMMENTS_TO_SHOW && showRepliesComment
            binding?.showViewMoreRepliesButton = shouldShowReplies
            showReplies(comment.getLatestReplies().take(MAXIMUM_REPLIES_COMMENTS_TO_SHOW))
        } else {
            commentLoader = loaderMap[comment.getCommentId()]

            val disposableGetComments = commentLoader!!.getComments()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .untilLifecycleEnd(view = itemView)
                .subscribe({ comments ->
                    showReplies(comments)
                }, {

                })

            val disposableShowLoadMore = commentLoader!!.showLoadMoreButton()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .untilLifecycleEnd(view = itemView)
                .subscribe({ shouldShow ->
                    binding?.showViewMoreRepliesButton = shouldShow && showRepliesComment
                }, {

                })
        }
    }

    private fun addItemClickListener(comment: EkoComment, position: Int) {
        itemView.setOnClickListener {
            itemClickListener?.onClickItem(comment, position)
        }

        itemView.ivAvatar.setOnClickListener {
            onClickAvatarListener(comment)
        }

        itemView.tvUserName.setOnClickListener {
            onClickAvatarListener(comment)
        }
    }

    private fun onClickAvatarListener(comment: EkoComment) {
        comment.getUser()?.let { user ->
            itemClickListener?.onClickAvatar(user)
        }
    }

    private fun addCommentActionListener(comment: EkoComment, position: Int) {
        binding?.ekoNewsFeedComment?.setCommentActionListener(object :
            EkoPostCommentView.ICommentActionListener {
            override fun showAllReplies() {
                showAllReplyListener?.onClickShowAllReplies(comment, position)
            }

            override fun showMoreAction() {
                showMoreActionListener?.onClickNewsFeedCommentShowMoreAction(comment, position)
            }

            override fun onClickReply(comment: EkoComment) {
                commentReplyClickListener?.onClickCommentReply(comment, position)
            }
        })
    }

    private fun showReplies(data: List<EkoComment>) {
        if (data.isNotEmpty()) {
            newsFeedCommentAdapter?.submitList(data.toPagedList(data.size))
        } else {
            newsFeedCommentAdapter?.submitList(null)
        }

    }

    private fun createAdapter() {
        newsFeedCommentAdapter = EkoPostCommentAdapter(
            null,
            null,
            showMoreActionListener,
            null,
            false,
            readOnlyMode
        )

        binding?.rvReply?.apply {
            layoutManager = LinearLayoutManager(itemView.context)
            adapter = newsFeedCommentAdapter
        }
    }

}