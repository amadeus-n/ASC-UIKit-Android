package com.ekoapp.ekosdk.uikit.community.newsfeed.listener

import com.ekoapp.ekosdk.uikit.community.domain.model.FileAttachment

interface ICreatePostFileActionListener {
    fun onRemoveFile(file: FileAttachment, position: Int)
}