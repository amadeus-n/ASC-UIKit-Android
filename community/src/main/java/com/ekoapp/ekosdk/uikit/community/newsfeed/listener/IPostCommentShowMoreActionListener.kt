package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.comment.EkoComment

interface IPostCommentShowMoreActionListener {
    fun onClickNewsFeedCommentShowMoreAction(comment: EkoComment, position: Int)
}