package com.ekoapp.ekosdk.uikit.chat.recent.fragment

import androidx.paging.PagedList
import com.ekoapp.ekosdk.EkoChannelRepository
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.channel.EkoChannel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.reactivex.Flowable
import org.junit.Assert
import org.junit.Test


class EkoRecentChatViewModelTest {

    @Test
    fun when_getRecentChat_expect_PagedListOfEkoChannel() {
        mockkStatic(EkoClient::class)
        val channelRepository: EkoChannelRepository = mockk()
        every { EkoClient.newChannelRepository() } returns channelRepository
        val mockList: PagedList<EkoChannel> = mockk()
        every { mockList.size } returns 5
        every {
            channelRepository.getChannelCollection().types(any()).filter(any()).build()
                .query()
        } returns Flowable.just(mockList)

        val viewModel = EkoRecentChatViewModel()
        val res = viewModel.getRecentChat().blockingFirst()
        Assert.assertEquals(res.size, 5)
    }
}