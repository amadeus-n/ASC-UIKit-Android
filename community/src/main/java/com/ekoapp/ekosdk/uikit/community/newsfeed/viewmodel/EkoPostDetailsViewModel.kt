package com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel

import androidx.paging.PagedList
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoCommentRepository
import com.ekoapp.ekosdk.EkoFeedRepository
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.feed.EkoPostTarget
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IAvatarClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostShareListener
import com.ekoapp.ekosdk.uikit.feed.settings.EkoFeedUISettings
import com.ekoapp.ekosdk.uikit.feed.settings.IPostShareClickListener
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import com.ekoapp.ekosdk.uikit.utils.SingleLiveData
import com.ekoapp.ekosdk.user.EkoUser
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class EkoPostDetailsViewModel : EkoBaseViewModel(), IPostShareListener {
    var newsFeed: EkoPost? = null
    var avatarClickListener: IAvatarClickListener? = null
    var postShareClickListener: IPostShareClickListener? = EkoFeedUISettings.postShareClickListener

    private val feedRepository: EkoFeedRepository = EkoClient.newFeedRepository()
    private val commentRepository: EkoCommentRepository = EkoClient.newCommentRepository()

    override val shareToMyTimelineActionRelay = SingleLiveData<Unit>()
    override val shareToGroupActionRelay = SingleLiveData<Unit>()
    override val shareToExternalAppActionRelay = SingleLiveData<Unit>()

    fun getComments(postId: String): Flowable<PagedList<EkoComment>> {
        return commentRepository.getCommentCollection()
            .post(postId)
            .parentId(null)
            .includeDeleted(true)
            .build()
            .query()
    }

    fun getPostDetails(id: String): Flowable<EkoPost> {
        return feedRepository.getPost(id)
    }

    fun deletePost(feed: EkoPost): Completable {
        return feedRepository.deletePost(feed.getPostId())
    }

    fun addComment(
        parentId: String?,
        commentId: String,
        postId: String,
        message: String
    ): Single<EkoComment> {
        return commentRepository.createComment(commentId)
            .post(postId)
            .parentId(parentId)
            .with()
            .text(message)
            .build()
            .send()
            .map {
                EkoClient.newFeedRepository().getPost(postId).ignoreElements().onErrorComplete()
                it
            }
    }

    fun deleteComment(commentId: String): Completable {
        return commentRepository.deleteComment(commentId)
    }

    //TODO remove after sdk (core) fix bug for fetch post data
    fun fetchPostData(postId: String): Completable {
        return feedRepository.getPost(postId).ignoreElements()
    }

    fun deleteComment(comment: EkoComment): Completable {
        return comment.delete()
    }

    fun getCurrentUser(): Flowable<EkoUser> {
        return EkoClient.getCurrentUser()
    }

    fun commentShowMoreActionClicked(feed: EkoPost, comment: EkoComment) {
        if (comment.getUserId() == EkoClient.getUserId()) {
            triggerEvent(EventIdentifier.SHOW_COMMENT_ACTION_BY_COMMENT_OWNER, comment)
        } else {
            //TODO uncomment after server side implementation
            /*val target = feed.getTarget()
            if (target is EkoPostTarget.COMMUNITY) {
                val community = target.getCommunity()
                if (community != null && community.getUserId() == EkoClient.getUserId()) {
                    triggerEvent(EventIdentifier.SHOW_COMMENT_ACTION_BY_ADMIN, comment)
                    return
                }
            }*/

            triggerEvent(EventIdentifier.SHOW_COMMENT_ACTION_BY_OTHER_USER, comment)
        }
    }

    fun postReaction(liked: Boolean, ekoPost: EkoPost): Completable {
        return if (liked) {
            ekoPost.react().addReaction("like")
        } else {
            ekoPost.react().removeReaction("like")
        }
    }

    fun feedShowMoreActionClicked(feed: EkoPost) {
        if (feed.getPostedUser()?.getUserId() == EkoClient.getUserId()!!) {
            triggerEvent(EventIdentifier.SHOW_FEED_ACTION_BY_FEED_OWNER, feed)
        } else {
            //TODO uncomment after server side implementation
            /*val target = feed.getTarget()
            if (target is EkoPostTarget.COMMUNITY) {
                val community = target.getCommunity()
                if (community != null && community.getUserId() == EkoClient.getUserId()) {
                    triggerEvent(EventIdentifier.SHOW_FEED_ACTION_BY_ADMIN, feed)
                    return
                }
            }*/
            triggerEvent(EventIdentifier.SHOW_FEED_ACTION_BY_OTHER_USER, feed)
        }
    }

    fun isReadOnlyPage(): Boolean {
        val target = newsFeed?.getTarget()

        if (target != null && target is EkoPostTarget.COMMUNITY) {
            val community = target.getCommunity()
            return community?.isJoined() == false
        }
        return false
    }

    fun reportPost(feed: EkoPost): Completable {
        return feed.report().flag()
    }

    fun unreportPost(feed: EkoPost): Completable {
        return feed.report().unflag()
    }

    fun reportComment(comment: EkoComment): Completable {
        return comment.report().flag()
    }

    fun unreportComment(comment: EkoComment): Completable {
        return comment.report().unflag()
    }
}