package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.comment.EkoComment

interface INewsFeedCommentShowMoreActionListener {
    fun onClickNewsFeedCommentShowMoreAction(comment: EkoComment, position: Int)
}