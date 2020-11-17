package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.file.EkoImage

interface INewsFeedImageClickListener {
    fun onClickImage(images: List<EkoImage>, position: Int)
}