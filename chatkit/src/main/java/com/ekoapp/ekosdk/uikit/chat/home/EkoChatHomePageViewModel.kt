package com.ekoapp.ekosdk.uikit.chat.home

import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import com.ekoapp.ekosdk.uikit.chat.home.callback.IDirectoryFragmentDelegate
import com.ekoapp.ekosdk.uikit.chat.home.callback.IRecentChatFragmentDelegate
import com.ekoapp.ekosdk.uikit.chat.home.callback.IRecentChatItemClickListener

class EkoChatHomePageViewModel: EkoBaseViewModel() {

    var recentChatItemClickListener: IRecentChatItemClickListener? = null
    var recentChatFragmentDelegate: IRecentChatFragmentDelegate? = null
    var directoryFragmentDelegate: IDirectoryFragmentDelegate? = null
}