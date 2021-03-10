package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.comment.EkoComment

interface IPostCommentShowAllReplyListener {
    fun onClickShowAllReplies(comment: EkoComment, position: Int)
}