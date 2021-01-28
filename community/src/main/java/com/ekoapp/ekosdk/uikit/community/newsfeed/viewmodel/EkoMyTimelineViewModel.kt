package com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel

import androidx.paging.PagedList
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoFeedRepository
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IAvatarClickListener
import com.ekoapp.ekosdk.user.EkoUser
import io.reactivex.Flowable

class EkoMyTimelineViewModel : EkoBaseFeedViewModel() {
    var avatarClickListener: IAvatarClickListener? = null

    override fun getFeed(): Flowable<PagedList<EkoPost>> {
        val feedRepository: EkoFeedRepository = EkoClient.newFeedRepository()
        return feedRepository
            .getMyFeed()
            .includeDeleted(false)
            .build()
            .query()
    }

    fun otherUser(user: EkoUser): Boolean {
        return user.getUserId() != EkoClient.getUserId()
    }
}