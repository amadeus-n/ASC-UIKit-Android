package com.ekoapp.ekosdk.uikit.community.members

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.paging.PagedList
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.community.membership.EkoCommunityMembership
import com.ekoapp.ekosdk.community.membership.query.EkoCommunityMembershipFilter
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import io.reactivex.Flowable

class EkoCommunityMembersViewModel : EkoBaseViewModel() {

    var communityId: String = ""
    val isPublic = ObservableBoolean(true)
    val searchString = ObservableField("")
    val emptyMembersList = ObservableBoolean(false)

    fun getCommunityMembers(): Flowable<PagedList<EkoCommunityMembership>> {
        val communityRepository = EkoClient.newCommunityRepository()
        return communityRepository.membership(communityId).getCollection()
            .filter(EkoCommunityMembershipFilter.MEMBER)
            .build()
            .query()
    }

    fun setPropertyChangeCallback() {
        searchString.addOnPropertyChanged {
            triggerEvent(EventIdentifier.SEARCH_STRING_CHANGED)
        }
    }
}