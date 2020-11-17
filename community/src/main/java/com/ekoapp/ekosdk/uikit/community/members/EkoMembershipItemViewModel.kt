package com.ekoapp.ekosdk.uikit.community.members

import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import io.reactivex.Completable

class EkoMembershipItemViewModel: EkoBaseViewModel() {

    fun reportUser(userId: String): Completable {
        val userRepository = EkoClient.newUserRepository()
        return userRepository.report(userId).flag()
    }
}