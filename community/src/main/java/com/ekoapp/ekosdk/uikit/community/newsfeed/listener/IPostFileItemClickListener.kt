package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.uikit.community.domain.model.FileAttachment

interface IPostFileItemClickListener {
    fun onClickFileItem(file: FileAttachment)
}