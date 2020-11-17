package com.ekoapp.ekosdk.uikit.community.explore.listener

import com.ekoapp.ekosdk.community.EkoCommunity

interface IEkoCommunityItemClickListener {
    fun onClick(category: EkoCommunity, position: Int)
}