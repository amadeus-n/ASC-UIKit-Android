package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.user.EkoUser

interface INewsFeedItemActionListener {
    fun onFeedAction(feed: EkoPost, position: Int)
    fun onClickItem(postId: String, position: Int)
    fun onCommentAction(feed: EkoPost, comment: EkoComment, position: Int)
    fun showAllReply(feed: EkoPost, comment: EkoComment, position: Int)
    fun onLikeAction(liked: Boolean, ekoPost: EkoPost, position: Int)
    fun onClickUserAvatar(feed: EkoPost, user: EkoUser, position: Int)
    fun onClickCommunity(community: EkoCommunity)
}