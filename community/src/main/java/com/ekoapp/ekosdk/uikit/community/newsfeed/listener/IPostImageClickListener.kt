package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.file.EkoImage

interface IPostImageClickListener {
    fun onClickImage(images: List<EkoImage>, position: Int)
}