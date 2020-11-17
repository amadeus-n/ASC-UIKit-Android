package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.community.EkoCommunity

interface ICreatePostCommunitySelectionListener {
    fun onClickCommunity(community: EkoCommunity, position: Int)
}