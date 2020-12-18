package com.ekoapp.ekosdk.uikit.community.members

import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import com.ekoapp.ekosdk.user.EkoUser
import io.reactivex.Completable
import io.reactivex.Flowable

class EkoMembershipItemViewModel : EkoBaseViewModel() {

    var communityId = ""

    fun reportUser(userId: EkoUser): Completable {
        return userId.report().flag()
    }

    fun unreportUser(userId: EkoUser): Completable {
        return userId.report().unflag()
    }

    fun getUser(userId: String): Flowable<EkoUser> {
        val userRepository = EkoClient.newUserRepository()
        return userRepository.getUser(userId)
    }

    fun removeUser(list: List<String>): Completable {
        val communityRepository = EkoClient.newCommunityRepository()
        return communityRepository.membership(communityId).removeUsers(list)
    }
}