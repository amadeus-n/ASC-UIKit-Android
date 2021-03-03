package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.comment.EkoComment

interface IPostCommentReplyClickListener {
    fun onClickCommentReply(comment: EkoComment, position: Int)
}