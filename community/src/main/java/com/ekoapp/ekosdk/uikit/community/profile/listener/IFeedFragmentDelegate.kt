package com.ekoapp.ekosdk.uikit.community.profile.listener

import com.ekoapp.ekosdk.uikit.community.newsfeed.fragment.EkoBaseFeedFragment

interface IFeedFragmentDelegate {
    fun getFeedFragment(): EkoBaseFeedFragment
}