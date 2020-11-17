package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.INewsFeedCommentItemClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.INewsFeedCommentShowAllReplyListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.INewsFeedCommentShowMoreActionListener

class EkoNewsFeedCommentAdapter(
    val itemClickListener: INewsFeedCommentItemClickListener?,
    private val showAllReplyListener: INewsFeedCommentShowAllReplyListener?,
    private val showMoreActionListener: INewsFeedCommentShowMoreActionListener?,
    private val preExpandCommentId: String? = null,
    private val readOnlyMode: Boolean = false
) : EkoBaseRecyclerViewAdapter<EkoComment>() {
    var subItemCount: Int? = null


    constructor(
        itemCount: Int,
        itemClickListener: INewsFeedCommentItemClickListener?,
        showAllReplyListener: INewsFeedCommentShowAllReplyListener?,
        showMoreActionListener: INewsFeedCommentShowMoreActionListener?,
        preExpandCommentId: String? = null,
        readOnlyMode: Boolean = false
    ) : this(itemClickListener, showAllReplyListener, showMoreActionListener, preExpandCommentId, readOnlyMode) {
        this.subItemCount = itemCount
    }

    override fun getLayoutId(position: Int, obj: EkoComment?): Int {
        if(obj?.isDeleted() == true)
            return R.layout.layout_news_feed_comment_item_deleted

        return R.layout.layout_news_feed_comment_item
    }

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == R.layout.layout_news_feed_comment_item_deleted)
            EkoNewsFeedCommentDeletedViewHolder(view)
        else {
            EkoNewsFeedCommentViewHolder(
                view,
                subItemCount,
                itemClickListener,
                showAllReplyListener,
                showMoreActionListener, preExpandCommentId, readOnlyMode
            )
        }
    }

    fun submitList(newList: List<EkoComment>) {
        setItems(newList, EkoNewsFeedCommentDiffUtil(list, newList))
    }

    class EkoNewsFeedCommentDiffUtil(private val oldList: List<EkoComment>,
                                     private val newList: List<EkoComment>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].getCommentId() == newList[newItemPosition].getCommentId()
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].getData() == newList[newItemPosition].getData()
        }

    }

    /*class EkoNewsFeedCommentDiffUtil : DiffUtil.ItemCallback<EkoComment>() {
        override fun areItemsTheSame(oldItem: EkoComment, newItem: EkoComment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EkoComment, newItem: EkoComment): Boolean {
            return oldItem == newItem
        }

    }*/
}