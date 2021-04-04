package com.ekoapp.ekosdk.uikit.community.setting

import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EkoCommunitySettingEssentialViewModel : EkoBaseViewModel() {
    var communityId = ""
    var community: EkoCommunity? = null

    fun setupData(communityId: String?, community: EkoCommunity?) {
        this.communityId = communityId ?: community?.getCommunityId() ?: ""
        this.community = community
    }

    fun getCommunity(communityId: String, community: EkoCommunity?, onCommunityLoaded: (EkoCommunity) -> Unit): Completable {
        if (community == null) {
            return EkoClient.newCommunityRepository().getCommunity(communityId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .firstOrError()
                    .doOnSuccess(onCommunityLoaded::invoke)
                    .ignoreElement()

        } else {
            onCommunityLoaded.invoke(community)
        }

        return Completable.complete()
    }

}