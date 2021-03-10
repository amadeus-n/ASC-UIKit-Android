package com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel

import androidx.paging.PagedList
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoFeedRepository
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.comment.EkoCommentReference
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostShareListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostItemClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostOptionClickListener
import com.ekoapp.ekosdk.uikit.feed.settings.EkoFeedUISettings
import com.ekoapp.ekosdk.uikit.feed.settings.IPostShareClickListener
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import com.ekoapp.ekosdk.uikit.utils.SingleLiveData
import io.reactivex.Completable
import io.reactivex.Flowable

abstract class EkoBaseFeedViewModel : EkoBaseViewModel(), IPostShareListener {

    var postOptionClickListener: IPostOptionClickListener? = null
    var postItemClickListener: IPostItemClickListener? = null
    var postShareClickListener: IPostShareClickListener? = EkoFeedUISettings.postShareClickListener

    override val shareToMyTimelineActionRelay = SingleLiveData<Unit>()
    override val shareToGroupActionRelay = SingleLiveData<Unit>()
    override val shareToExternalAppActionRelay = SingleLiveData<Unit>()

    abstract fun getFeed(): Flowable<PagedList<EkoPost>>?

    fun deletePost(post: EkoPost): Completable {
        val feedRepository: EkoFeedRepository = EkoClient.newFeedRepository()
        return feedRepository.deletePost(post.getPostId())
    }

    fun commentShowMoreActionClicked(feed: EkoPost, comment: EkoComment) {
        if (comment.getUserId() == EkoClient.getUserId()) {
            triggerEvent(EventIdentifier.SHOW_COMMENT_ACTION_BY_COMMENT_OWNER, comment)
        } else {
            //TODO uncomment after server side implementation
            /*  val target = feed.getTarget()
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

    fun feedShowShareOptionsActionClicked(post: EkoPost) {
        triggerEvent(EventIdentifier.SHOW_SHARE_OPTIONS, post)
    }

    fun feedShowMoreActionClicked(feed: EkoPost) {
        //TODO uncomment after server side implementation
        /*   val target = feed.getTarget()
           if (target is EkoPostTarget.COMMUNITY) {
               val community = target.getCommunity()
               if (community != null && community.getUserId() == EkoClient.getUserId()) {
                   triggerEvent(EventIdentifier.SHOW_FEED_ACTION_BY_ADMIN, feed)
                   return
               }
           }*/
        if (feed.getPostedUser()?.getUserId() == EkoClient.getUserId()!!) {
            triggerEvent(EventIdentifier.SHOW_FEED_ACTION_BY_FEED_OWNER, feed)
        } else {
            triggerEvent(EventIdentifier.SHOW_FEED_ACTION_BY_OTHER_USER, feed)
        }

    }

    fun deleteComment(comment: EkoComment): Completable {
        return comment.delete()
                .concatWith(Completable.defer {
                    val postId = (comment.getReference() as EkoCommentReference.Post).getPostId()
                    EkoClient.newFeedRepository()
                            .getPost(postId)
                            .ignoreElements()
                            .onErrorComplete()
                })
    }

    fun postReaction(liked: Boolean, ekoPost: EkoPost): Completable {
        return if (liked) {
            ekoPost.react().addReaction("like")
        } else {
            ekoPost.react().removeReaction("like")
        }
    }

    fun reportPost(post: EkoPost): Completable {
        return post.report().flag()
    }

    fun unreportPost(post: EkoPost): Completable {
        return post.report().unflag()
    }

    fun reportComment(comment: EkoComment): Completable {
        return comment.report().flag()
    }

    fun unreportComment(comment: EkoComment): Completable {
        return comment.report().unflag()
    }

}