package com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel

import androidx.paging.PagedList
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IAvatarClickListener
import io.reactivex.Flowable

class EkoGlobalFeedViewModel : EkoBaseFeedViewModel() {
    var avatarClickListener: IAvatarClickListener? = null

    override fun getFeed(): Flowable<PagedList<EkoPost>> {
        val feedRepository = EkoClient.newFeedRepository()
        return feedRepository.getGlobalFeed()
            .build()
            .query()
    }
}