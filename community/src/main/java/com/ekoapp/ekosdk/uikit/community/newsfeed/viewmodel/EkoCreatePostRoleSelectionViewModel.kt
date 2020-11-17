package com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel

import androidx.paging.PagedList
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.community.query.EkoCommunityFilter
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import com.ekoapp.ekosdk.user.EkoUser
import io.reactivex.Flowable

class EkoCreatePostRoleSelectionViewModel : EkoBaseViewModel() {

    fun getUser(): EkoUser {
        return EkoClient.getCurrentUser().blockingFirst()
    }


    fun getCommunityList(): Flowable<PagedList<EkoCommunity>> {
        val communityRepository = EkoClient.newCommunityRepository()
        return communityRepository.getCommunityCollection()
            .filter(EkoCommunityFilter.MEMBER)
            .includeDeleted(false)
            .build()
            .query()
    }

}