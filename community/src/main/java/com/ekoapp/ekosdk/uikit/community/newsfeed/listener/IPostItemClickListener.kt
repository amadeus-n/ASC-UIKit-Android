package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.feed.EkoPost

interface IPostItemClickListener {
    fun onClickPostItem(post: EkoPost)
}