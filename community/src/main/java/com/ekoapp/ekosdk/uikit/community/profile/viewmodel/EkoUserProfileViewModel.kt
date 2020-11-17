package com.ekoapp.ekosdk.uikit.community.profile.viewmodel

import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import com.ekoapp.ekosdk.uikit.community.profile.listener.IFeedFragmentDelegate
import com.ekoapp.ekosdk.user.EkoUser
import io.reactivex.Flowable
import io.reactivex.Single

class EkoUserProfileViewModel : EkoBaseViewModel() {
    var feedFragmentDelegate: IFeedFragmentDelegate? = null
    var userId: String? = null

    fun getUser(): Flowable<EkoUser> {
        return if (userId == null || EkoClient.getUserId() == userId) {
            EkoClient.getCurrentUser()
        } else {
            EkoClient.newUserRepository().getUser(userId!!)
        }
    }

    fun isLoggedInUser() : Boolean {
        return userId == null || EkoClient.getUserId() == userId
    }


}