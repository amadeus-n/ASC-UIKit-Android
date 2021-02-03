package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.uikit.community.utils.ShareType
import com.ekoapp.ekosdk.uikit.utils.SingleLiveData

interface INewsFeedShareListener {

    val shareToMyTimelineActionRelay: SingleLiveData<Unit>
    val shareToGroupActionRelay: SingleLiveData<Unit>
    val shareToExternalAppActionRelay: SingleLiveData<Unit>

    fun observeShareToMyTimelinePage(): SingleLiveData<Unit> = shareToMyTimelineActionRelay
    fun observeShareToPage(): SingleLiveData<Unit> = shareToGroupActionRelay
    fun observeShareToExternalApp(): SingleLiveData<Unit> = shareToExternalAppActionRelay

    fun navigateShareTo(type: ShareType) {
        when (type) {
            ShareType.MY_TIMELINE -> {
                shareToMyTimelineActionRelay.postValue(Unit)
            }
            ShareType.GROUP -> {
                shareToGroupActionRelay.postValue(Unit)
            }
            ShareType.EXTERNAL -> {
                shareToExternalAppActionRelay.postValue(Unit)
            }
        }
    }

}