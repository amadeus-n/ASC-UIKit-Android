package com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel

import androidx.paging.PagedList
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.feed.EkoPost
import io.reactivex.Flowable

class EkoGlobalFeedViewModel : EkoBaseFeedViewModel() {

    override fun getFeed(): Flowable<PagedList<EkoPost>> {
        val feedRepository = EkoClient.newFeedRepository()
        return feedRepository.getGlobalFeed()
            .build()
            .query()
    }
}