package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.comment.EkoComment

interface INewsFeedCommentShowAllReplyListener {
    fun onClickShowAllReplies(comment: EkoComment, position: Int)
}