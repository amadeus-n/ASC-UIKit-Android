package com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel

import androidx.databinding.ObservableBoolean
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import com.ekoapp.ekosdk.uikit.community.home.listener.IGlobalFeedFragmentDelegate
import com.ekoapp.ekosdk.uikit.community.home.listener.IMyCommunityListPreviewFragmentDelegate
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.ICreatePostButtonClickListener

class EkoNewsFeedViewModel : EkoBaseViewModel() {

    var createPostButtonClickListener: ICreatePostButtonClickListener? = null
    var globalFeedFragmentDelegate: IGlobalFeedFragmentDelegate? = null
    var myCommunityListPreviewFragmentDelegate: IMyCommunityListPreviewFragmentDelegate? = null
    val emptyCommunityList = ObservableBoolean(false)
    val emptyGlobalFeed = ObservableBoolean(false)
}