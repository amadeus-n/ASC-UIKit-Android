package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.uikit.community.newsfeed.model.FeedImage

interface ICreatePostImageActionListener {
    fun onRemoveImage(feedImage: FeedImage, position: Int)
}