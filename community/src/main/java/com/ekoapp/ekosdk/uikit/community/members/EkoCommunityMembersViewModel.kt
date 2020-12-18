package com.ekoapp.ekosdk.uikit.community.members

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.paging.PagedList
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.community.membership.EkoCommunityMembership
import com.ekoapp.ekosdk.community.membership.query.EkoCommunityMembershipFilter
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import com.ekoapp.ekosdk.uikit.community.data.SelectMemberItem
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EkoCommunityMembersViewModel : EkoBaseViewModel() {

    var communityId: String = ""
    var community: EkoCommunity? = null
    val isPublic = ObservableBoolean(true)
    val searchString = ObservableField("")
    val emptyMembersList = ObservableBoolean(false)
    val selectMembersList = ArrayList<SelectMemberItem>()
    val membersSet = HashSet<String>()

    fun getCommunityMembers(): Flowable<PagedList<EkoCommunityMembership>> {
        val communityRepository = EkoClient.newCommunityRepository()
        return communityRepository.membership(communityId).getCollection()
            .filter(EkoCommunityMembershipFilter.MEMBER)
            .build()
            .query()
    }

    fun getCommunityDetail(): Flowable<EkoCommunity> {
        val communityRepository = EkoClient.newCommunityRepository()
        return communityRepository.getCommunity(communityId)
    }

    fun getCommunityModerators(): Flowable<PagedList<EkoCommunityMembership>> {
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

    fun handleAddRemoveMembers(newList: ArrayList<SelectMemberItem>) {
        val addedMembers = arrayListOf<String>()
        val removedMembers = arrayListOf<String>()
        val toRemoveMembers = arrayListOf<SelectMemberItem>()
        for (item in selectMembersList) {
            if (newList.contains(item)) {
                newList.remove(item)
            }else {
                removedMembers.add(item.id)
                toRemoveMembers.add(item)
            }
        }
        for (item in newList) {
           addedMembers.add(item.id)
        }
        if (removedMembers.isNotEmpty()) {
            removeUsersFromCommunity(removedMembers)
        }
        if (addedMembers.isNotEmpty()) {
            addMembersToCommunity(addedMembers)
        }

        for (member in toRemoveMembers) {
            updateSelectedMembersList(member)
        }
    }

    private fun removeUsersFromCommunity(list: List<String>) {
        val communityRepository = EkoClient.newCommunityRepository()
        addDisposable(communityRepository.membership(communityId)
            .removeUsers(list)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe())
    }

    private fun addMembersToCommunity(list: List<String>) {
        val communityRepository = EkoClient.newCommunityRepository()
        addDisposable(communityRepository.membership(communityId)
            .addUsers(list)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe())
    }

    fun updateSelectedMembersList(member: SelectMemberItem) {
        selectMembersList.remove(member)
        membersSet.remove(member.id)
    }
}