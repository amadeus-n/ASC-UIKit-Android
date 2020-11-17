package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.user.EkoUser

interface INewsFeedActionAvatarClickListener {
    fun onClickUserAvatar(user: EkoUser)
}