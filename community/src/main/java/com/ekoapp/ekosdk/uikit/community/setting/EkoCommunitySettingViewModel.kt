package com.ekoapp.ekosdk.uikit.community.setting

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import com.ekoapp.ekosdk.uikit.common.formatCount
import io.reactivex.Completable
import io.reactivex.Flowable

class EkoCommunitySettingViewModel : EkoBaseViewModel() {

    val communityId = ObservableField("")
    var ekoCommunity: EkoCommunity? = null
    val isModerator = ObservableBoolean(false)
    val membersCount = ObservableField("0")
    val isPublic = ObservableBoolean(false)
    val checkedPermission = ObservableBoolean(false)

    fun leaveCommunity(): Completable {
        val communityRepository = EkoClient.newCommunityRepository()
        return communityRepository.leaveCommunity(communityId.get() ?: "")
    }

    fun closeCommunity(): Completable {
        val communityRepository = EkoClient.newCommunityRepository()
        return communityRepository.deleteCommunity(communityId.get() ?: "")
    }

    fun getCommunityDetail(): Flowable<EkoCommunity> {
        val communityRepository = EkoClient.newCommunityRepository()
        return communityRepository.getCommunity(communityId.get()!!)
    }

    fun setCommunity(ekoCommunity: EkoCommunity) {
        communityId.set(ekoCommunity.getCommunityId())
        membersCount.set(ekoCommunity.getMemberCount().toDouble().formatCount())
        isPublic.set(ekoCommunity.isPublic())
    }
}