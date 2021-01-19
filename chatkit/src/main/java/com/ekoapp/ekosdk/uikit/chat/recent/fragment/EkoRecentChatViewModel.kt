package com.ekoapp.ekosdk.uikit.chat.recent.fragment

import androidx.paging.PagedList
import com.ekoapp.ekosdk.EkoChannelRepository
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.channel.EkoChannel
import com.ekoapp.ekosdk.channel.query.EkoChannelFilter
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import com.ekoapp.ekosdk.uikit.chat.home.callback.IRecentChatItemClickListener
import io.reactivex.Flowable

class EkoRecentChatViewModel : EkoBaseViewModel() {

    var recentChatItemClickListener: IRecentChatItemClickListener? = null

    fun getRecentChat(): Flowable<PagedList<EkoChannel>> {
        val channelRepository: EkoChannelRepository = EkoClient.newChannelRepository()
        val types = listOf(EkoChannel.Type.CONVERSATION)

        return channelRepository.getChannelCollection()
                .types(types)
                .filter(EkoChannelFilter.MEMBER)
                .build()
                .query()
    }
}