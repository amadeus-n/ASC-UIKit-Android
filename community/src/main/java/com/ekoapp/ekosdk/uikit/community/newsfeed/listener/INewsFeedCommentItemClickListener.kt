package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.comment.EkoComment

interface INewsFeedCommentItemClickListener {
    fun onClickItem(comment: EkoComment, position: Int)
}