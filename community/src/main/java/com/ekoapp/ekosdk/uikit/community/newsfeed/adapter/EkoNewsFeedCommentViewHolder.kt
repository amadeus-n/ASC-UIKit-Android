package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.common.toPagedList
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.INewsFeedCommentItemClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.INewsFeedCommentShowAllReplyListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.INewsFeedCommentShowMoreActionListener
import com.ekoapp.ekosdk.uikit.community.views.newsfeed.EkoNewsFeedCommentView


class EkoNewsFeedCommentViewHolder(
    itemView: View,
    private val itemCount: Int?,
    private val itemClickListener: INewsFeedCommentItemClickListener?,
    private val showAllReplyListener: INewsFeedCommentShowAllReplyListener?,
    private val showMoreActionListener: INewsFeedCommentShowMoreActionListener?,
    private val preExpandCommentId: String? = null,
    val readOnlyMode: Boolean
) : RecyclerView.ViewHolder(itemView), EkoBaseRecyclerViewAdapter.IBinder<EkoComment>  {
    private var newsFeedCommentAdapter: EkoNewsFeedCommentAdapter? = null
    private val ekoNewsFeedComment: EkoNewsFeedCommentView = itemView.findViewById(R.id.ekoNewsFeedComment)
    private val rvReply: RecyclerView = itemView.findViewById(R.id.rvReply)

    override fun bind(data: EkoComment?, position: Int) {
         data?.let { ekoComment ->
             ekoNewsFeedComment.setComment(ekoComment)
             ekoNewsFeedComment.setOnExpandClickListener(View.OnClickListener {
                 itemClickListener?.onClickItem(ekoComment, position)
             })
             if(preExpandCommentId != null && ekoComment.getCommentId() == preExpandCommentId) {
                 handleShowAllReply(ekoComment)
             }
             ekoNewsFeedComment.setReadOnlyMode(readOnlyMode)
            /* if(readOnlyMode)
                 ekoNewsFeedComment.enableReadOnlyMode()*/
             setVerticalDivider(ekoComment, position)
             addCommentActionListener(ekoComment, position)
             addItemClickListener(ekoComment, position)
         }
    }

    private fun addItemClickListener(comment: EkoComment, position: Int) {
        itemView.setOnClickListener {
            itemClickListener?.onClickItem(comment, position)
        }
    }

    private fun addCommentActionListener(comment: EkoComment, position: Int) {
        ekoNewsFeedComment.setCommentActionListener(object : EkoNewsFeedCommentView.ICommentActionListener {
            override fun showAllReplies() {
                if(showAllReplyListener == null) {
                    handleShowAllReply(comment)

                }else {
                    showAllReplyListener.onClickShowAllReplies(comment, position)
                }
            }

            override fun showMoreAction() {
                showMoreActionListener?.onClickNewsFeedCommentShowMoreAction(comment, position)
            }
        })
    }

    private fun handleShowAllReply(comment: EkoComment) {
        ekoNewsFeedComment.setViewAllReplyVisibility(View.GONE)
        //TODO Pass post id for get comment
//        val disposable = EkoClient.newCommentRepository()
//            .getCommentCollection()
//            .post(comment.id)
//            .parentId(comment.getParentId())
//            .build()
//            .query()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                showReplies(it)
//            }, {
//
//            })
    }

    private fun setVerticalDivider(data: EkoComment, position: Int) {
        var visibility = View.GONE
        if(data.getChildrenNumber() > 0) {
            visibility = View.VISIBLE
        }else if(itemCount != null && position < itemCount -1) {
            visibility = View.VISIBLE
        }
        ekoNewsFeedComment.setVerticalDividerVisibility(visibility)

    }

    private fun showReplies(data: List<EkoComment>) {
        initEkoPostCommentRecyclerview(data.size)
        setReplies(data.toPagedList(data.size))
    }

    private fun initEkoPostCommentRecyclerview(size: Int) {
        newsFeedCommentAdapter = EkoNewsFeedCommentAdapter(size, itemClickListener, showAllReplyListener, showMoreActionListener, null, readOnlyMode)
//        val space8 = itemView.context.resources.getDimensionPixelSize(R.dimen.eight)
//        val spaceItemDecoration = EkoRecyclerViewItemDecoration(space8, 0, space8, 0)
//        rvReply.addItemDecoration(spaceItemDecoration)
        rvReply.layoutManager = LinearLayoutManager(itemView.context)
        rvReply.adapter = newsFeedCommentAdapter
    }

    private fun setReplies(pagedList: PagedList<EkoComment>){
        newsFeedCommentAdapter?.submitList(pagedList)
    }
}