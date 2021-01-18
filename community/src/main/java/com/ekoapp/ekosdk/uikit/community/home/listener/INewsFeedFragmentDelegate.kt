package com.ekoapp.ekosdk.uikit.community.home.listener

import com.ekoapp.ekosdk.uikit.community.newsfeed.fragment.EkoNewsFeedFragment

interface INewsFeedFragmentDelegate {
    fun getNewsFeedFragment(): EkoNewsFeedFragment
}