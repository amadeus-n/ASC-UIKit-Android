package com.ekoapp.ekosdk.uikit.community.domain.repository

import androidx.paging.PagedList
import com.ekoapp.ekosdk.uikit.community.domain.model.Channel
import com.ekoapp.ekosdk.uikit.community.domain.model.ChannelCategory
import com.ekoapp.ekosdk.uikit.community.domain.model.NewsFeed

interface IChannelRepository {
    fun getRecommendedChannels(): List<Channel>
    fun getTrendingChannels() : List<Channel>
    fun getChannelCategory() : List<ChannelCategory>
    fun getChannelCategory(parentCategory: String) : List<ChannelCategory>
}